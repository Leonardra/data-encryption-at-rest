package com.github.leonardra.data_encryption_at_rest.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DefaultResponse {
    private String message;
    private LocalDateTime timestamp;
}
