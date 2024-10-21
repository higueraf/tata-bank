package com.tata.bank.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.UUID;

@Getter
@Setter
public class CustomerRequestDto {

    private UUID id;

    @NotBlank
    @Size(max = 100)
    private String name;

    private String gender;

    private int age;

    @Size(max = 50)
    private String identification;

    @Size(max = 200)
    private String address;

    @Size(max = 20)
    private String phone;

    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    private String state;
}
