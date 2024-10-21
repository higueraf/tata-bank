package com.tata.account.controller;

import com.tata.account.dto.*;
import com.tata.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping
    public ResponseEntity<ApiResponseDto<AccountResponseDto>> createAccount(
            @RequestBody AccountRequestDto accountDto) {
        AccountResponseDto createdAccount = accountService.createAccount(accountDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDto<>(true, "Account created successfully", createdAccount));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<AccountResponseDto>> updateAccount(
            @PathVariable UUID id, @RequestBody AccountRequestDto accountDto) {
        AccountResponseDto updatedAccount = accountService.updateAccount(id, accountDto);
        return ResponseEntity.ok(new ApiResponseDto<>(true, "Account updated successfully", updatedAccount));
    }

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<AccountResponseDto>>> getAllAccounts() {
        List<AccountResponseDto> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(new ApiResponseDto<>(true, "Accounts retrieved successfully", accounts));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<AccountResponseDto>> getAccountById(@PathVariable UUID id) {
        Optional<AccountResponseDto> account = accountService.getAccountById(id);
        return account.map(value -> ResponseEntity.ok(new ApiResponseDto<>(true, "Account retrieved successfully", value)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponseDto<>(false, "Account not found", null)));
    }

    @PostMapping("/filtered-page")
    public ResponseEntity<ApiResponseDto<PaginationResponseDto<AccountResponseDto>>> getFilteredUsers(
            @RequestBody FilteredRequestDto filteredRequestDto) {
        Page<AccountResponseDto> customersDto = accountService.getFilteredAccounts(filteredRequestDto);
        PaginationResponseDto<AccountResponseDto> paginationResponseDto = new PaginationResponseDto<>(
                customersDto.getContent(),
                customersDto.getTotalElements(),
                customersDto.getNumber(),
                customersDto.getSize(),
                customersDto.getTotalPages()
        );
        return ResponseEntity.ok(
                new ApiResponseDto<>(
                        true,
                        "Customers retrieved successfully",
                        paginationResponseDto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteAccount(@PathVariable UUID id) {
        accountService.deleteAccount(id);
        return ResponseEntity.ok(new ApiResponseDto<>(true, "Account deleted successfully", null));
    }

}
