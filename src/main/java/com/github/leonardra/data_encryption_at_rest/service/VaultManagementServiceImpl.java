package com.github.leonardra.data_encryption_at_rest.service;

import com.github.leonardra.data_encryption_at_rest.dtos.responses.DefaultResponse;
import com.github.leonardra.data_encryption_at_rest.dtos.responses.VaultResponse;
import com.github.leonardra.data_encryption_at_rest.models.User;
import com.github.leonardra.data_encryption_at_rest.models.Vault;
import com.github.leonardra.data_encryption_at_rest.security.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class VaultManagementServiceImpl implements VaultManagementService {
    private final VaultService vaultService;
    private final AuthenticationService authenticationService;

    @Override
    public DefaultResponse createVault(Vault vault) {
        User user = authenticationService.getCurrentlyLoggedInUser();
        vault.setUser(user);
        vaultService.save(vault);
        return DefaultResponse.builder()
                .message("Vault created successfully")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    public VaultResponse getVaultById(String id) {
        User user = authenticationService.getCurrentlyLoggedInUser();
        Optional<Vault> vaultOptional = vaultService.findByIdAndUser(id, user);
        if(vaultOptional.isEmpty()) {
            throw new RuntimeException("Vault not found");
        }
        Vault vault = vaultOptional.get();
        return VaultResponse.builder()
                .id(vault.getId())
                .key(vault.getTitle())
                .value(vault.getContent())
                .build();
    }
}
