package com.api.rest.dto;

public record LoginResponse(String acessToken, Long expireIn) {
}
