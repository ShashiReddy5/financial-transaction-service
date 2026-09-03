package com.shashireddy.fintx.dto;

import com.shashireddy.fintx.model.Transaction;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Request/response payloads for the transaction API. Kept as records in a
 * single file since they are simple, immutable data carriers with no
 * behavior of their own.
 */
public final class TransactionDtos {

    private TransactionDtos() {
    }

    public record TransactionRequest(
            @NotBlank String accountId,
            @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal amount,
            @NotBlank String currency,
            @NotNull Transaction.Type type
    ) {
    }

    public record TransactionResponse(
            String id,
            String accountId,
            BigDecimal amount,
            String currency,
            Transaction.Type type,
            Transaction.Status status,
            Instant createdAt
    ) {
        public static TransactionResponse from(Transaction tx) {
            return new TransactionResponse(
                    tx.getId(),
                    tx.getAccountId(),
                    tx.getAmount(),
                    tx.getCurrency(),
                    tx.getType(),
                    tx.getStatus(),
                    tx.getCreatedAt()
            );
        }
    }
}
