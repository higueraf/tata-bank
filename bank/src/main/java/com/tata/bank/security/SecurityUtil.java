package com.tata.bank.security;

import com.tata.bank.dto.DataSessionDto;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static DataSessionDto getDataSession() {
        return (DataSessionDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
