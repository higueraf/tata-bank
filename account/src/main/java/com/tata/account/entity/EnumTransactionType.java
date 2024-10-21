package com.tata.account.entity;

import lombok.Getter;

@Getter
public enum EnumTransactionType {
    DEPOSIT("IN"),
    WITHDRAWAL("OUT"),
    TRANSFER_IN("IN"),
    TRANSFER_OUT("OUT"),
    PAYMENT("OUT"),
    FEE("OUT");

    private final String direction;

    EnumTransactionType(String direction) {
        this.direction = direction;
    }

}
