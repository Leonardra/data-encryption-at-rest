package com.github.leonardra.data_encryption_at_rest.controllers;

import com.github.leonardra.data_encryption_at_rest.dtos.responses.DefaultResponse;
import com.github.leonardra.data_encryption_at_rest.dtos.responses.VaultResponse;
import com.github.leonardra.data_encryption_at_rest.models.Vault;
import com.github.leonardra.data_encryption_at_rest.service.VaultManagementService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.slf4j.LoggerFactory.getLogger;


@RestController
@RequestMapping("/api/v1/vault")
@RequiredArgsConstructor
public class VaultController {
    private static final Logger LOG = getLogger(VaultController.class);
    private final VaultManagementService vaultManagementService;

    @PostMapping("/secrets")
    public ResponseEntity<DefaultResponse> storeSecret(@RequestBody Vault vault){
        return ResponseEntity.ok(vaultManagementService.createVault(vault));
    }


    @GetMapping("/secrets/{id}")
    public ResponseEntity<VaultResponse> getSecret(@PathVariable String id){
        return ResponseEntity.ok(vaultManagementService.getVaultById(id));
    }
}
