package com.github.leonardra.data_encryption_at_rest.dtos;

public record EncryptionResult(
        byte[] ciphertext,
        byte[] encryptedDek,
        byte[] iv,
        byte[] wrapIv
) {}
