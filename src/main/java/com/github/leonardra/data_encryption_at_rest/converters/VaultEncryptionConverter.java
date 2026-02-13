package com.github.leonardra.data_encryption_at_rest.converters;

import com.github.leonardra.data_encryption_at_rest.dtos.EncryptionResult;
import com.github.leonardra.data_encryption_at_rest.utils.EncryptionContext;
import com.github.leonardra.data_encryption_at_rest.utils.EncryptionUtil;
import com.github.leonardra.data_encryption_at_rest.utils.SpringContext;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.slf4j.LoggerFactory.getLogger;

@Converter
@RequiredArgsConstructor
public class VaultEncryptionConverter implements AttributeConverter<String, String> {
    private static final Logger LOG = getLogger(VaultEncryptionConverter.class);
    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) return null;

        try {
            EncryptionContext context = SpringContext.getBean(EncryptionContext.class);
            EncryptionUtil util = SpringContext.getBean(EncryptionUtil.class);

            EncryptionResult res = util.encryptWithEnvelope(
                    attribute.getBytes(StandardCharsets.UTF_8),
                    context.getMasterKey()
            );


            return Base64.getEncoder().encodeToString(
                    ByteBuffer.allocate(res.iv().length + res.ciphertext().length)
                            .put(res.iv()).put(res.ciphertext()).array()
            );
        } catch (Exception e) {
            throw new RuntimeException("failed to encrypt for database storage", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;

        try {
            EncryptionContext context = SpringContext.getBean(EncryptionContext.class);
            SecretKey mk = context.getMasterKey();
            if (mk == null) {
                LOG.error("Master Key is NULL during JPA conversion for Vault entity");
                return null;
            }
            byte[] decoded = Base64.getDecoder().decode(dbData);
            ByteBuffer buffer = ByteBuffer.wrap(decoded);

            byte[] iv = new byte[12];
            buffer.get(iv);
            byte[] ciphertext = new byte[buffer.remaining()];
            buffer.get(ciphertext);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, context.getMasterKey(), new GCMParameterSpec(128, iv));

            return new String(cipher.doFinal(ciphertext), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("failed to decrypt from database storage", e);
        }
    }
}
