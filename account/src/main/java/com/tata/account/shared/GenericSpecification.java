package com.tata.account.shared;

import com.tata.account.dto.FilteredRequestDto;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GenericSpecification<T> {

    private final Class<T> entityClass;

    public GenericSpecification(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public Specification<T> getSpecification(FilteredRequestDto filteredRequestDto) {
        List<Specification<T>> specifications = new ArrayList<>();

        if (filteredRequestDto.getUuids() != null && !filteredRequestDto.getUuids().isEmpty()) {
            filteredRequestDto.getUuids().forEach(uuid -> {
                try {
                    if (uuid.getValue() != null) { // Verificar si NO es nulo primero
                        UUID uuidValue = UUID.fromString(uuid.getValue());
                        specifications.add(SpecificationUtil.equal(uuid.getFieldName(), uuidValue));
                    } else {
                        specifications.add(SpecificationUtil.isNullUuid(uuid.getFieldName()));
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid UUID: " + uuid.getValue());
                }
            });
        }

        if (filteredRequestDto.getSearchFields() != null && !filteredRequestDto.getSearchFields().isEmpty()) {
            filteredRequestDto.getSearchFields().forEach(string -> {
                specifications.add(SpecificationUtil.like(string.getFieldName(), "%" + filteredRequestDto.getSearchString() + "%"));
            });
        }

        if (hasFieldRecursive(this.entityClass, "deletedBy")) {
            specifications.add(SpecificationUtil.isNullUuid("deletedBy")); // Usar isNullUuid
        }

        return SpecificationUtil.and(specifications);
    }

    private boolean hasFieldRecursive(Class<?> clazz, String fieldName) {
        if (clazz == null) {
            return false;
        }

        try {
            clazz.getDeclaredField(fieldName);
            return true;
        } catch (NoSuchFieldException e) {
            return hasFieldRecursive(clazz.getSuperclass(), fieldName);
        }
    }
}