package com.github.leonardra.data_encryption_at_rest.repositories;

import com.github.leonardra.data_encryption_at_rest.models.User;
import com.github.leonardra.data_encryption_at_rest.models.Vault;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VaultRepository extends JpaRepository<Vault, String>{
    Optional<Vault> findByIdAndUser(String id, User user);
}
