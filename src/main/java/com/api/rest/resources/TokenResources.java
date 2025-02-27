package com.api.rest.resources;

import com.api.rest.dto.LoginRequestDTO;
import com.api.rest.dto.LoginResponseDTO;
import com.api.rest.model.Role;
import com.api.rest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/login")
public class TokenResources {

    @Autowired
    private final JwtEncoder jwtEncoder;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    public TokenResources(JwtEncoder jwtEncoder, UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
        this.passwordEncoder = bCryptPasswordEncoder;
    }

    @PostMapping
    public ResponseEntity<LoginResponseDTO> login (@RequestBody LoginRequestDTO loginRequest) {
        var user = userRepository.findByUsername(loginRequest.username());

        if (user.isEmpty() || !user.get().isLoginCorret(loginRequest, passwordEncoder) ) {
            throw new BadCredentialsException("user or password is invalid!");
        }

        var now = Instant.now();
        var expireIn = 300L;

        var scopes = user.get().getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.joining(" "));

        var claims = JwtClaimsSet.builder()
                .issuer("mybackend")
                .subject(user.get().getUserId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expireIn))
                .claim("scope", scopes)
                .build();

        var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return ResponseEntity.ok(new LoginResponseDTO(jwtValue, expireIn));
    }
}
