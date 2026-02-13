package com.github.leonardra.data_encryption_at_rest.models;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Key {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(columnDefinition = "BYTEA")
    private byte[] systemSalt;
    @Column(columnDefinition = "BYTEA")
    private byte[] userHalf;
    @Column(columnDefinition = "BYTEA")
    private byte[] userHalfIv;
}
