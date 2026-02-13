package com.github.leonardra.data_encryption_at_rest.controllers;


import com.github.leonardra.data_encryption_at_rest.dtos.requests.AuthRequest;
import com.github.leonardra.data_encryption_at_rest.dtos.responses.AuthResponse;
import com.github.leonardra.data_encryption_at_rest.dtos.requests.CreateUserRequest;
import com.github.leonardra.data_encryption_at_rest.dtos.responses.CreateUserResponse;
import com.github.leonardra.data_encryption_at_rest.dtos.responses.DefaultResponse;
import com.github.leonardra.data_encryption_at_rest.security.AuthenticationService;
import com.github.leonardra.data_encryption_at_rest.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserManagementService userManagementService;
    private final AuthenticationService authenticationService;

    @PostMapping
    public CreateUserResponse createUser(@RequestBody  CreateUserRequest createUserRequest) throws Exception {
        return userManagementService.createUser(createUserRequest);
    }

    @PostMapping("/login")
    public AuthResponse login(@Validated @RequestBody AuthRequest request){
        return authenticationService.authenticate(request);
    }
}
