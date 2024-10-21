package com.tata.account.service;

import com.tata.account.dto.ReportResponseDto;
import com.tata.account.entity.AccountTransaction;
import com.tata.account.entity.Customer;
import com.tata.account.repository.AccountTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private AccountTransactionRepository transactionRepository;

    public List<ReportResponseDto> getAccountStatement(LocalDate initialDate, LocalDate endDate,
                                                       UUID accountId, UUID customerId) {
        List<AccountTransaction> transactions;

        if (accountId != null) {
            transactions = transactionRepository.findByAccountIdAndDateBetweenAndDeletedAtIsNull(
                    accountId, initialDate, endDate);
        } else if (customerId != null) {
            transactions = transactionRepository.findByAccountCustomerIdAndDateBetweenAndDeletedAtIsNull(
                    customerId, initialDate, endDate);
        } else {
            transactions = transactionRepository.findByDateBetweenAndDeletedAtIsNull(
                    initialDate, endDate);
        }

        return transactions.stream()
                .map(this::mapToReportResponse)
                .collect(Collectors.toList());
    }

    private ReportResponseDto mapToReportResponse(AccountTransaction transaction) {
        Customer customer = transaction.getAccount().getCustomer();

        return new ReportResponseDto(
                transaction.getDate(),
                customer.getName(),
                transaction.getAccount().getAccountNumber(),
                transaction.getAccount().getAccountType().name(),
                transaction.getAccount().getInitialBalance(),
                transaction.getAccount().getAccountState(),
                transaction.getAmount(),
                transaction.getBalance()
        );
    }
}
