package com.github.leonardra.data_encryption_at_rest.dtos.responses;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateUserResponse {
    private CharSequence key;
    private String message;
}
