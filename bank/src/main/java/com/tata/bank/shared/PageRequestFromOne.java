package com.tata.bank.shared;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PageRequestFromOne extends PageRequest {

    private PageRequestFromOne(int page, int size, Sort sort) {
        super(page - 1, size, sort);
    }

    public static PageRequest of(int page, int size, Sort sort) {
        return new PageRequestFromOne(page, size, sort);
    }

    public static PageRequest of(int page, int size) {
        return of(page, size, Sort.unsorted());
    }

    @Override
    public int getPageNumber() {
        return super.getPageNumber() + 1;
    }

}
