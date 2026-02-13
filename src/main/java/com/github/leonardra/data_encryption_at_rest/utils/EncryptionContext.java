package com.github.leonardra.data_encryption_at_rest.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import javax.crypto.SecretKey;


@Component
@RequestScope
@RequiredArgsConstructor
public class EncryptionContext {
    @Getter
    private SecretKey masterKey;
    private final EncryptionUtil encryptionUtil;

    public void init(byte[] wrappedKu, String password, byte[] iv, byte[] systemSalt) throws Exception {
        byte[] ku = EncryptionUtil.unwrapUserHalf(wrappedKu, password, iv);
        this.masterKey = encryptionUtil.deriveMasterKey(ku, systemSalt);
    }

}
