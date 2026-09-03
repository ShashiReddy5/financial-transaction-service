package com.shashireddy.fintx.controller;

import com.shashireddy.fintx.dto.TransactionDtos.TransactionRequest;
import com.shashireddy.fintx.dto.TransactionDtos.TransactionResponse;
import com.shashireddy.fintx.model.Transaction;
import com.shashireddy.fintx.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> create(@Valid @RequestBody TransactionRequest request) {
        Transaction saved = service.process(request);
        return ResponseEntity.ok(TransactionResponse.from(saved));
    }

    @GetMapping("/{id}")
    public TransactionResponse getById(@PathVariable String id) {
        return TransactionResponse.from(service.getById(id));
    }

    @GetMapping
    public Page<TransactionResponse> list(
            @RequestParam(required = false) String accountId,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<Transaction> page = (accountId == null || accountId.isBlank())
                ? service.list(pageable)
                : service.listByAccount(accountId, pageable);

        return page.map(TransactionResponse::from);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNotFound(NoSuchElementException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }
}
