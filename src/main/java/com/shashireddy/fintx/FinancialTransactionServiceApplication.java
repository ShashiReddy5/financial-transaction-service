package com.shashireddy.fintx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the financial transaction demo service.
 *
 * This is a deliberately simplified, self-contained reference implementation:
 * it demonstrates the shape of an event-driven Spring Boot 3 transaction
 * service (validation, persistence, JWT-secured endpoints, an outbound event
 * hook for a message broker) without requiring Kafka, AWS, or an external
 * database to run locally. See the README for what is simplified and how the
 * pieces map onto a production deployment.
 */
@SpringBootApplication
public class FinancialTransactionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinancialTransactionServiceApplication.class, args);
    }
}
