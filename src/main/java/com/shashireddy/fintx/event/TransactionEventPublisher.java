package com.shashireddy.fintx.event;

import com.shashireddy.fintx.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Publishes a domain event whenever a transaction is created or its status
 * changes. In production this is where a Kafka producer would publish to a
 * topic (e.g. "transactions.processed") for downstream consumers such as
 * settlement, fraud analytics, or notifications.
 *
 * The demo ships with a logging-only implementation so the service runs
 * without a broker. Swap in a KafkaTemplate-backed implementation of this
 * interface (guarded by a "kafka" Spring profile) to wire up real streaming.
 */
public interface TransactionEventPublisher {

    void publish(Transaction transaction);

    @Component
    class LoggingTransactionEventPublisher implements TransactionEventPublisher {

        private static final Logger log = LoggerFactory.getLogger(LoggingTransactionEventPublisher.class);

        @Override
        public void publish(Transaction transaction) {
            log.info("transaction.event id={} accountId={} type={} status={} amount={} {}",
                    transaction.getId(),
                    transaction.getAccountId(),
                    transaction.getType(),
                    transaction.getStatus(),
                    transaction.getAmount(),
                    transaction.getCurrency());
        }
    }
}
