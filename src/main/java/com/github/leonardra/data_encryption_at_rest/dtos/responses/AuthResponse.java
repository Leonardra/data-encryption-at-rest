package com.github.leonardra.data_encryption_at_rest.dtos.responses;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String accessToken;
    private long expiredAt;
    private AuthUserResponse user;
}
