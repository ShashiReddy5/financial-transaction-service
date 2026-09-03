package com.shashireddy.fintx.repository;

import com.shashireddy.fintx.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, String> {

    Page<Transaction> findByAccountId(String accountId, Pageable pageable);
}
