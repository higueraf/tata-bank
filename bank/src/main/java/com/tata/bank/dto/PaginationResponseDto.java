package com.tata.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PaginationResponseDto<T> {
    private List<T> content;
    private long totalElements;
    private int page;
    private int pageSize;
    private int totalPages;

}
