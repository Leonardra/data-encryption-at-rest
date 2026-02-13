package com.github.leonardra.data_encryption_at_rest.repositories;

import com.github.leonardra.data_encryption_at_rest.models.Key;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeyRepository extends JpaRepository<Key,String> {
}
