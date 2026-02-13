package com.github.leonardra.data_encryption_at_rest.security;


import com.github.leonardra.data_encryption_at_rest.dtos.requests.AuthRequest;
import com.github.leonardra.data_encryption_at_rest.dtos.responses.AuthResponse;
import com.github.leonardra.data_encryption_at_rest.models.User;

public interface AuthenticationService {

    AuthResponse authenticate(AuthRequest authRequest);
    User getCurrentlyLoggedInUser();
}
