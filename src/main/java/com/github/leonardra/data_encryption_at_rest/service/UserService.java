package com.github.leonardra.data_encryption_at_rest.service;

import com.github.leonardra.data_encryption_at_rest.models.User;

import java.util.Optional;

public interface UserService {
    User save(User user);
    Optional<User> findByUsername(String username);
}
