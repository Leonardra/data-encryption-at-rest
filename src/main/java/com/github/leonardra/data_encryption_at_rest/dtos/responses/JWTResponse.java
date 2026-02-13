package com.github.leonardra.data_encryption_at_rest.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class JWTResponse {
    private String jwt;
    private long expiredAt;
}
