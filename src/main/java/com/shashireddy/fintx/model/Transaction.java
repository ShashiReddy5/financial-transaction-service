package com.shashireddy.fintx.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction {

    public enum Type { DEPOSIT, WITHDRAWAL, TRANSFER }

    /**
     * APPROVED  - passed validation and risk rules, ready to settle.
     * PENDING_REVIEW - exceeds the auto-approval threshold, held for manual review.
     * REJECTED  - failed validation (e.g. non-positive amount).
     */
    public enum Status { APPROVED, PENDING_REVIEW, REJECTED }

    @Id
    private String id;

    @Column(nullable = false, updatable = false)
    private String accountId;

    @Column(nullable = false, updatable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, updatable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private Type type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected Transaction() {
        // required by JPA
    }

    public Transaction(String accountId, BigDecimal amount, String currency, Type type) {
        this.id = UUID.randomUUID().toString();
        this.accountId = accountId;
        this.amount = amount;
        this.currency = currency;
        this.type = type;
        this.status = Status.PENDING_REVIEW;
        this.createdAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public String getAccountId() {
        return accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public Type getType() {
        return type;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
