package com.tata.bank.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Person extends AuditableEntity {

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private EnumGender gender;

    private int age;

    @Column(nullable = false, unique = true)
    private String identification;

    private String address;

    private String phone;
}
