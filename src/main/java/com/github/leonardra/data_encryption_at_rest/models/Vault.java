package com.github.leonardra.data_encryption_at_rest.models;

import com.github.leonardra.data_encryption_at_rest.converters.VaultEncryptionConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vault{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String title;

    @Convert(converter = VaultEncryptionConverter.class)
    @Column(columnDefinition = "TEXT")
    private String content;
    @ManyToOne
    @JoinColumn(referencedColumnName = "id", name = "user_id", nullable = false)
    private User user;
}
