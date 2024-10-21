package com.tata.account.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Table(name = "\"customer\"")
public class Customer extends Person {

    private String password;

    private EnumCustomerState state;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

}