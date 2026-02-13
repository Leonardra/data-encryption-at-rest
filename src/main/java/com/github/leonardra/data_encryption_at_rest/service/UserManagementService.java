package com.github.leonardra.data_encryption_at_rest.service;

import com.github.leonardra.data_encryption_at_rest.dtos.requests.CreateUserRequest;
import com.github.leonardra.data_encryption_at_rest.dtos.responses.CreateUserResponse;

public interface UserManagementService {
    CreateUserResponse createUser(CreateUserRequest createUserRequest) throws Exception;
}
