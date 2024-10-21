package com.tata.account.dto;

import com.tata.account.entity.EnumTransactionType;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountTransactionRequestDto {
    private LocalDateTime date;
    private EnumTransactionType type;
    private double amount;
    private double balance;
    private UUID accountId;
}
