package com.tata.bank.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UserRegisterRequestDto {
    private UserDto user;
    private List<UUID> roleIds;
}
