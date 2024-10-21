package com.tata.bank.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequestDto {

    @NotBlank
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters long")
    private String username;

    @NotBlank
    @Size(min = 3, max = 100, message = "Password must be between 3 and 100 characters long")
    private String password;

}
