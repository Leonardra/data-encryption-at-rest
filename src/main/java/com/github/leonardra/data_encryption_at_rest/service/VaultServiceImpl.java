package com.github.leonardra.data_encryption_at_rest.service;

import com.github.leonardra.data_encryption_at_rest.models.User;
import com.github.leonardra.data_encryption_at_rest.models.Vault;
import com.github.leonardra.data_encryption_at_rest.repositories.VaultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class VaultServiceImpl implements VaultService{
    private final VaultRepository vaultRepository;

    @Override
    public Vault save(Vault vault) {
        return vaultRepository.save(vault);
    }

    @Override
    public Optional<Vault> findByIdAndUser(String id, User user) {
        return vaultRepository.findByIdAndUser(id, user);
    }
}
