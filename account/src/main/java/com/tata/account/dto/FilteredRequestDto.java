package com.tata.account.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FilteredRequestDto {
    private List<FieldFilter> uuids;
    private List<SearchField> searchFields;
    private List<SortOrder> sortOrders;
    private int page;
    private int pageSize;
    private String searchString;

    @Getter
    @Setter
    public static class FieldFilter {
        private String fieldName;
        private String value;
    }

    @Getter
    @Setter
    public static class SearchField {
        private String fieldName;
    }

    @Getter
    @Setter
    public static class SortOrder {
        private String fieldName;
        private int direction;
    }
}
