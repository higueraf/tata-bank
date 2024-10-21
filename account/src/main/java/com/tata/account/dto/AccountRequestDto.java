package com.tata.account.dto;

import com.tata.account.entity.EnumAccountState;
import com.tata.account.entity.EnumAccountType;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountRequestDto {
    private String accountNumber;
    private EnumAccountType accountType;
    private double initialBalance;
    private EnumAccountState accountState;
    private UUID customerId;
}
