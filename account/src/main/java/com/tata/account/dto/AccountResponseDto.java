package com.tata.account.dto;

import com.tata.account.entity.EnumAccountState;
import com.tata.account.entity.EnumAccountType;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class AccountResponseDto {
    private UUID id;
    private String accountNumber;
    private EnumAccountType accountType;
    private double initialBalance;
    private EnumAccountState accountState;
    private UUID customerId;
}
