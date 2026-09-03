package com.shashireddy.fintx.service;

import com.shashireddy.fintx.dto.TransactionDtos.TransactionRequest;
import com.shashireddy.fintx.event.TransactionEventPublisher;
import com.shashireddy.fintx.model.Transaction;
import com.shashireddy.fintx.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

@Service
public class TransactionService {

    /**
     * Transactions at or above this amount are held for manual review rather
     * than auto-approved. A stand-in for a real risk/fraud rules engine.
     */
    static final BigDecimal AUTO_APPROVAL_LIMIT = new BigDecimal("10000.00");

    private final TransactionRepository repository;
    private final TransactionEventPublisher eventPublisher;

    public TransactionService(TransactionRepository repository, TransactionEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Transaction process(TransactionRequest request) {
        Transaction transaction = new Transaction(
                request.accountId(),
                request.amount(),
                request.currency().toUpperCase(),
                request.type()
        );

        transaction.setStatus(decideStatus(transaction));

        Transaction saved = repository.save(transaction);
        eventPublisher.publish(saved);
        return saved;
    }

    private Transaction.Status decideStatus(Transaction transaction) {
        if (transaction.getAmount().compareTo(AUTO_APPROVAL_LIMIT) >= 0) {
            return Transaction.Status.PENDING_REVIEW;
        }
        return Transaction.Status.APPROVED;
    }

    @Transactional(readOnly = true)
    public Transaction getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No transaction with id " + id));
    }

    @Transactional(readOnly = true)
    public Page<Transaction> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Transaction> listByAccount(String accountId, Pageable pageable) {
        return repository.findByAccountId(accountId, pageable);
    }
}
