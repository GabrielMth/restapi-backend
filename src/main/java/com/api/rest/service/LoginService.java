package com.api.rest.service;

import com.api.rest.dto.LoginRequestDTO;
import com.api.rest.dto.LoginResponseDTO;
import com.api.rest.model.Role;
import com.api.rest.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    // Cache para armazenar temporariamente o refresh token (não persistido no banco)
    private final Map<String, String> refreshTokenCache = new HashMap<>();

    private static final long ACCESS_TOKEN_EXPIRE_IN = 3600L; // 1 hora para o token de acesso
    private static final long REFRESH_TOKEN_EXPIRE_IN = 18000L; // 5 horas para o refresh token
    private static final long CLEANUP_INTERVAL = 1000L;

    public LoginService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtEncoder jwtEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        var userOpt = userRepository.findByUsername(loginRequest.username());

        if (userOpt.isEmpty() || !userOpt.get().isLoginCorrect(loginRequest, passwordEncoder)) {
            throw new BadCredentialsException("Usuário ou senha inválidos!");
        }

        var user = userOpt.get();
        Instant now = Instant.now();

        // Geração do token de acesso
        JwtClaimsSet accessClaims = JwtClaimsSet.builder()
                .issuer("mybackend")
                .subject(user.getUserId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(ACCESS_TOKEN_EXPIRE_IN))
                .claim("name", user.getUsername())
                .claim("roles", user.getRoles().stream().map(Role::getName).toList())
                .build();

        String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(accessClaims)).getTokenValue();

        // Geração do refresh token
        String refreshToken = UUID.randomUUID().toString(); // Gerando um refresh token único
        refreshTokenCache.put(refreshToken, user.getUserId().toString()); // Armazenando o refresh token em cache por 5 horas

        return new LoginResponseDTO(accessToken, ACCESS_TOKEN_EXPIRE_IN, "Bearer", user.getUsername(), refreshToken);
    }

    // Endpoint para renovar o access token usando o refresh token
    public String refreshToken(String refreshToken) {
        if (!refreshTokenCache.containsKey(refreshToken)) {
            throw new BadCredentialsException("Refresh token inválido ou expirado!");
        }

        String userId = refreshTokenCache.get(refreshToken);
        var userOpt = userRepository.findById(UUID.fromString(userId));

        if (userOpt.isEmpty()) {
            throw new BadCredentialsException("Usuário não encontrado.");
        }

        var user = userOpt.get();
        Instant now = Instant.now();

        // Gerando novo token de acesso
        JwtClaimsSet accessClaims = JwtClaimsSet.builder()
                .issuer("mybackend")
                .subject(user.getUserId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(ACCESS_TOKEN_EXPIRE_IN))
                .claim("name", user.getUsername())
                .claim("roles", user.getRoles().stream().map(Role::getName).toList())
                .build();

        String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(accessClaims)).getTokenValue();

        return accessToken;
    }

    private void cleanupExpiredTokens() {

        long currentTime = System.currentTimeMillis();
        refreshTokenCache.entrySet().removeIf(entry -> {
            long tokenTimestamp = Long.parseLong(entry.getValue());
            return currentTime - tokenTimestamp > REFRESH_TOKEN_EXPIRE_IN * 1000;
        });
    }

    @PostConstruct
    public void startCleanupScheduler() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::cleanupExpiredTokens, CLEANUP_INTERVAL, CLEANUP_INTERVAL, TimeUnit.SECONDS);
    }

}
