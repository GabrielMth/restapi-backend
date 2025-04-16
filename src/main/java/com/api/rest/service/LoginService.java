package com.api.rest.service;

import com.api.rest.dto.LoginRequestDTO;
import com.api.rest.dto.LoginResponseDTO;
import com.api.rest.model.Role;
import com.api.rest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    public LoginService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtEncoder jwtEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
    }

    private static final long EXPIRE_IN = 300L;


    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        var userOpt = userRepository.findByUsername(loginRequest.username());

        if (userOpt.isEmpty() ||
                !userOpt.get().isLoginCorret(loginRequest, passwordEncoder)) {
            throw new BadCredentialsException("Usuário ou senha inválidos!");
        }

        var user = userOpt.get();
        Instant now = Instant.now();

        // monta a claim de escopos a partir das roles
        var scopes = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("mybackend")
                .subject(user.getUserId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(EXPIRE_IN))
                .claim("scope", scopes)
                .claim("name", user.getUsername()) // Adiciona o nome
                .claim("roles", user.getRoles().stream().map(Role::getName).toList()) // Adiciona lista de roles
                .build();

        var token = jwtEncoder
                .encode(JwtEncoderParameters.from(claims))
                .getTokenValue();

        return new LoginResponseDTO(
                token,
                EXPIRE_IN,
                "Bearer",
                user.getUsername());
    }


}
