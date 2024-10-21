package com.tata.bank.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateUserDto {

    private UUID id;

    @NotBlank
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters long")
    private String username;

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Size(max = 100)
    private String lastName;

    @NotBlank
    @Email
    @Size(max = 100)
    private String email;

    private String password;

    @Size(max = 200)
    private String address;

    @Size(max = 5)
    private String phoneNumberCode;

    @Size(max = 20)
    private String phoneNumber;

    @Min(0)
    private int accessFailedCount;

    private LocalDateTime birthdate;

    @Size(max = 128)
    private String title;

}
