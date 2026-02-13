package com.github.leonardra.data_encryption_at_rest.service;

import com.github.leonardra.data_encryption_at_rest.models.User;
import com.github.leonardra.data_encryption_at_rest.models.Vault;

import java.util.Optional;

public interface VaultService {
    Vault save(Vault vault);
    Optional<Vault> findByIdAndUser(String id, User user);
}
