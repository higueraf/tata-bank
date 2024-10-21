package com.tata.bank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Account extends AuditableEntity {

    @Column(nullable = false, unique = true)
    private String accountNumber;

    private EnumAccountType accountType;

    private double initialBalance;

    private EnumAccountState accountState;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
}