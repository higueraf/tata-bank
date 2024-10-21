package com.tata.account.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
public class AccountTransaction extends AuditableEntity {

    private LocalDateTime date;

    private EnumTransactionType type;

    private double amount;

    private double balance;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
}