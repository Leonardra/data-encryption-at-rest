package com.github.leonardra.data_encryption_at_rest.dtos.requests;


import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}
