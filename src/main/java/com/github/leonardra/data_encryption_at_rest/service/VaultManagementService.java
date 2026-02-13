package com.github.leonardra.data_encryption_at_rest.service;

import com.github.leonardra.data_encryption_at_rest.dtos.responses.DefaultResponse;
import com.github.leonardra.data_encryption_at_rest.dtos.responses.VaultResponse;
import com.github.leonardra.data_encryption_at_rest.models.Vault;

public interface VaultManagementService {
    DefaultResponse createVault(Vault vault);
    VaultResponse getVaultById(String id);
}
