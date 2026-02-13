package com.github.leonardra.data_encryption_at_rest.dtos.responses;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VaultResponse {
    private String id;
    private String key;
    private String value;
}
