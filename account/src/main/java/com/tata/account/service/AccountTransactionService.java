package com.tata.account.service;

import com.tata.account.dto.AccountTransactionRequestDto;
import com.tata.account.dto.AccountTransactionResponseDto;
import com.tata.account.dto.FilteredRequestDto;
import com.tata.account.entity.Account;
import com.tata.account.entity.AccountTransaction;
import com.tata.account.repository.AccountRepository;
import com.tata.account.repository.AccountTransactionRepository;
import com.tata.account.security.SecurityUtil;
import com.tata.account.shared.GenericSpecification;
import com.tata.account.shared.PageRequestFromOne;
import com.tata.account.shared.SpecificationUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AccountTransactionService {

    @Autowired
    private AccountTransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public AccountTransactionResponseDto createTransaction(AccountTransactionRequestDto transactionDto) {
        UUID createdBy = UUID.fromString(SecurityUtil.getDataSession().getUserId());

        try {
            Account account = accountRepository.findById(transactionDto.getAccountId())
                    .orElseThrow(() -> new IllegalArgumentException("Account not found"));

            double previousBalance = getPreviousBalance(account);

            double transactionAmount = getSignedAmount(transactionDto);

            double newBalance = previousBalance + transactionAmount;

            if (newBalance < 0) {
                throw new IllegalArgumentException("Insufficient funds for this transaction.");
            }

            AccountTransaction transaction = modelMapper.map(transactionDto, AccountTransaction.class);
            transaction.setAccount(account);
            transaction.setAmount(transactionAmount);
            transaction.setBalance(newBalance);
            transaction.setCreatedBy(createdBy);
            transaction.setCreatedAt(LocalDateTime.now());

            AccountTransaction savedTransaction = transactionRepository.save(transaction);
            return modelMapper.map(savedTransaction, AccountTransactionResponseDto.class);

        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Transaction data integrity violation.", e);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error creating transaction: " + e.getMessage(), e);
        }
    }

    private double getPreviousBalance(Account account) {
        return transactionRepository.findTopByAccountOrderByCreatedAtDesc(account)
                .map(AccountTransaction::getBalance)
                .orElse(account.getInitialBalance());
    }

    private double getSignedAmount(AccountTransactionRequestDto transactionDto) {
        double amount = transactionDto.getAmount();

        if (transactionDto.getType().getDirection().equals("OUT")) {
            return -amount;
        }
        return amount;
    }


    public List<AccountTransactionResponseDto> getTransactionsByAccountId(UUID accountId) {
        try {
            List<AccountTransaction> transactions = transactionRepository.findByAccountIdAndDeletedAtIsNull(accountId);
            return transactions.stream()
                    .map(transaction -> modelMapper.map(transaction, AccountTransactionResponseDto.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new IllegalArgumentException("Error fetching transactions: " + e.getMessage(), e);
        }
    }

    public Page<AccountTransactionResponseDto> getFilteredTransactions(FilteredRequestDto filteredRequestDto) {
        try {
            Specification<AccountTransaction> transactionSpec = new GenericSpecification<>(AccountTransaction.class)
                    .getSpecification(filteredRequestDto);
            Sort sort = SpecificationUtil.createSort(filteredRequestDto.getSortOrders());
            Pageable pageable = PageRequestFromOne.of(
                    filteredRequestDto.getPage(),
                    filteredRequestDto.getPageSize(),
                    sort
            );

            Page<AccountTransaction> transactions = transactionRepository.findAll(transactionSpec, pageable);
            return transactions.map(transaction -> modelMapper.map(transaction, AccountTransactionResponseDto.class));
        } catch (Exception e) {
            throw new IllegalArgumentException("Error fetching filtered transactions: " + e.getMessage(), e);
        }
    }

    public void deleteTransaction(UUID id) {
        UUID deletedBy = UUID.fromString(SecurityUtil.getDataSession().getUserId());

        try {
            AccountTransaction transaction = transactionRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

            transaction.setDeletedBy(deletedBy);
            transaction.setDeletedAt(LocalDateTime.now());

            transactionRepository.save(transaction);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error deleting transaction: " + e.getMessage(), e);
        }
    }
}
