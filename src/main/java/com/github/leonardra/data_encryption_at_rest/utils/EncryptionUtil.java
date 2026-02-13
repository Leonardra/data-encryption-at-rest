package com.github.leonardra.data_encryption_at_rest.utils;

import com.github.leonardra.data_encryption_at_rest.dtos.EncryptionResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@Service
public class EncryptionUtil {

    @Value("${app.security.global-secret}")
    private String globalSecret;
    private static final String KEY_ALGORITHM = "AES";
    private static final int KEY_SIZE_BITS = 256;

    public static byte[] generateRandomKey() {
        byte[] key = new byte[32];
        new SecureRandom().nextBytes(key);
        return key;
    }

    public SecretKey deriveMasterKey(byte[] ku, byte[] ks) throws Exception {
        byte[] combinedInput = ByteBuffer.allocate(ku.length + ks.length + globalSecret.length())
                .put(ku)
                .put(ks)
                .put(globalSecret.getBytes(StandardCharsets.UTF_8))
                .array();

        byte[] salt = "SystemMasterKeySalt".getBytes(StandardCharsets.UTF_8);
        byte[] info = "MasterKeyDerivation".getBytes(StandardCharsets.UTF_8);

        Mac hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec saltKey = new SecretKeySpec(salt, "HmacSHA256");
        hmac.init(saltKey);
        byte[] prk = hmac.doFinal(combinedInput);

        hmac.init(new SecretKeySpec(prk, "HmacSHA256"));
        hmac.update(info);
        hmac.update((byte) 0x01);
        byte[] masterKeyBytes = Arrays.copyOf(hmac.doFinal(), KEY_SIZE_BITS / 8);

        return new SecretKeySpec(masterKeyBytes, KEY_ALGORITHM);
    }

    private char[] byteToCharArray(byte[] bytes) {
        char[] chars = new char[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            chars[i] = (char) bytes[i];
        }
        return chars;
    }

    public static EncryptionResult wrapUserHalf(byte[] ku, String rawPassword) throws Exception {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);

        char[] passwordChars = rawPassword.toCharArray();
        try {
            SecretKey wrappingKey = deriveKeyFromPassword(passwordChars, salt);
            byte[] iv = new byte[12];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, wrappingKey, new GCMParameterSpec(128, iv));
            byte[] encryptedKu = cipher.doFinal(ku);

            byte[] ciphertextWithSalt = ByteBuffer.allocate(salt.length + encryptedKu.length)
                    .put(salt)
                    .put(encryptedKu)
                    .array();

            return new EncryptionResult(ciphertextWithSalt, null, iv, null);
        }finally {
            Arrays.fill(passwordChars, '0');
        }
    }

    public static byte[] unwrapUserHalf(byte[] wrappedKuWithSalt, String rawPassword, byte[] iv) throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.wrap(wrappedKuWithSalt);
        byte[] salt = new byte[16];
        byteBuffer.get(salt);
        byte[] ciphertext = new byte[byteBuffer.remaining()];
        byteBuffer.get(ciphertext);

        char[] passwordChars = rawPassword.toCharArray();
        try {
            SecretKey wrappingKey = deriveKeyFromPassword(passwordChars, salt);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, wrappingKey, new GCMParameterSpec(128, iv));

            return cipher.doFinal(ciphertext);
        } finally {
            Arrays.fill(passwordChars, '0');
        }
    }

    private static  SecretKey deriveKeyFromPassword(char[] password, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password, salt, 600000, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] derivedKey = factory.generateSecret(spec).getEncoded();

        return new SecretKeySpec(derivedKey, "AES");
    }

    public EncryptionResult encryptWithEnvelope(byte[] plaintext, SecretKey masterKey) throws Exception {
        if (masterKey == null) {
            throw new IllegalStateException("Encryption Key is missing. Ensure the vault is unlocked.");
        }

        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.ENCRYPT_MODE, masterKey, spec);

        byte[] ciphertext = cipher.doFinal(plaintext);

        return new EncryptionResult(ciphertext, null, iv, null);
    }
}
