package com.tata.account.security;

import com.tata.account.dto.DataSessionDto;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static DataSessionDto getDataSession() {
        return (DataSessionDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
