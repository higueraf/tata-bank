package com.tata.account.dto;

import com.tata.account.entity.EnumAccountState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ReportResponseDto {
    private LocalDateTime date;
    private String customerName;
    private String accountNumber;
    private String accountType;
    private double initialBalance;
    private EnumAccountState accountState;
    private double transactionAmount;
    private double availableBalance;
}
