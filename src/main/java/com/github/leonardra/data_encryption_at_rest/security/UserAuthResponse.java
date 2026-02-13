package com.github.leonardra.data_encryption_at_rest.security;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class UserAuthResponse {
    private String accessToken;
    private long expiredAt;
}
