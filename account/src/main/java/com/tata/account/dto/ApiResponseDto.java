package com.tata.account.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponseDto<T> {
    private boolean success;
    private String message;
    private T data;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String debugMessage;

    public ApiResponseDto(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public void setDebugMessage(String debugMessage) {
        this.debugMessage = debugMessage;
    }

}
