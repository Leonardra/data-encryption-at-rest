package com.github.leonardra.data_encryption_at_rest.service;

import com.github.leonardra.data_encryption_at_rest.models.Key;
import com.github.leonardra.data_encryption_at_rest.repositories.KeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class KeyServiceImpl implements KeyService {
    private final KeyRepository keyRepository;

    @Override
    public Key save(Key key) {
        return keyRepository.save(key);
    }
}
