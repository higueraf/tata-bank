package com.tata.account.controller;

import com.tata.account.dto.AccountTransactionRequestDto;
import com.tata.account.dto.AccountTransactionResponseDto;
import com.tata.account.dto.ApiResponseDto;
import com.tata.account.service.AccountTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
public class AccountTransactionController {

    @Autowired
    private AccountTransactionService transactionService;

    @PostMapping
    public ResponseEntity<ApiResponseDto<AccountTransactionResponseDto>> createTransaction(
            @RequestBody AccountTransactionRequestDto transactionDto) {
        AccountTransactionResponseDto createdTransaction = transactionService.createTransaction(transactionDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDto<>(true, "Transaction created successfully", createdTransaction));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<ApiResponseDto<List<AccountTransactionResponseDto>>> getTransactionsByAccountId(
            @PathVariable UUID accountId) {
        List<AccountTransactionResponseDto> transactions = transactionService.getTransactionsByAccountId(accountId);
        return ResponseEntity.ok(
                new ApiResponseDto<>(true, "Transactions retrieved successfully", transactions));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteTransaction(@PathVariable UUID id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.ok(new ApiResponseDto<>(true, "Transaction deleted successfully", null));
    }
}
