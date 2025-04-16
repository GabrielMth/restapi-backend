package com.api.rest.dto;

public record LoginResponseDTO(String acessToken, Long expireIn, String tokenType, String name) {
}
