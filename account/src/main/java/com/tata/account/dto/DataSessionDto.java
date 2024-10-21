package com.tata.account.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataSessionDto {
    private String userId;
    private String companyId;
    private String language;
    private String email;
    private String roles;
    private String name;
    private String lastName;
    private String userPhoneNumber;
    private String userPhoneNumberCode;
    private String userCountryId;
    private String userCountryName;
    private String companyName;
    private String companyCountryId;
    private String companyCountryName;
    private String companyMainCategoryId;
    private String companyCurrentBusinessCategoryId;
    private String token;
}
