package com.shashireddy.fintx.service;

import com.shashireddy.fintx.dto.TransactionDtos.TransactionRequest;
import com.shashireddy.fintx.event.TransactionEventPublisher;
import com.shashireddy.fintx.model.Transaction;
import com.shashireddy.fintx.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    private TransactionRepository repository;
    private TransactionEventPublisher eventPublisher;
    private TransactionService service;

    @BeforeEach
    void setUp() {
        repository = mock(TransactionRepository.class);
        eventPublisher = mock(TransactionEventPublisher.class);
        service = new TransactionService(repository, eventPublisher);

        // save() just returns whatever it was given, like a real repository would
        when(repository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void amountBelowLimitIsAutoApproved() {
        TransactionRequest request = new TransactionRequest(
                "acct-1", new BigDecimal("250.00"), "usd", Transaction.Type.DEPOSIT);

        Transaction result = service.process(request);

        assertThat(result.getStatus()).isEqualTo(Transaction.Status.APPROVED);
        assertThat(result.getCurrency()).isEqualTo("USD");
    }

    @Test
    void amountAtOrAboveLimitIsHeldForReview() {
        TransactionRequest request = new TransactionRequest(
                "acct-1", new BigDecimal("10000.00"), "usd", Transaction.Type.WITHDRAWAL);

        Transaction result = service.process(request);

        assertThat(result.getStatus()).isEqualTo(Transaction.Status.PENDING_REVIEW);
    }

    @Test
    void processedTransactionIsPublishedAsAnEvent() {
        TransactionRequest request = new TransactionRequest(
                "acct-2", new BigDecimal("50.00"), "usd", Transaction.Type.DEPOSIT);

        Transaction result = service.process(request);

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(eventPublisher).publish(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(result.getId());
    }
}
