package com.github.leonardra.data_encryption_at_rest.service;

import com.github.leonardra.data_encryption_at_rest.models.Key;

public interface KeyService {
    Key save(Key key);
}
