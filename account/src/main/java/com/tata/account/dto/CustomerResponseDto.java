package com.tata.account.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.UUID;

@Getter
@Setter
public class CustomerResponseDto {

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

    private String state;
}
