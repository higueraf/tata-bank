package com.tata.bank.shared;

import com.tata.bank.dto.FilteredRequestDto;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import java.util.List;
import java.util.UUID;

public class SpecificationUtil {

    public static <T> Specification<T> like(String attribute, String value) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Path<String> path = getPath(root, attribute, String.class);
            return cb.like(
                    cb.lower(cb.coalesce(path, "")), // Reemplazar nulo por ""
                    "%" + value.toLowerCase() + "%"
            );
        };
    }

    public static <T> Specification<T> equal(String attribute, String value) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Path<?> path = getPath(root, attribute, String.class); // Asumiendo que los atributos de tipo String se manejan como String.class
            return cb.equal(path, value);
        };
    }

    public static <T> Specification<T> equal(String attribute, UUID value) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Path<UUID> path = getPath(root, attribute, UUID.class);
            return cb.equal(path, value);
        };
    }

    public static <T> Specification<T> isNull(String attribute) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Path<?> path = getPath(root, attribute, Object.class);
            return cb.isNull(path);
        };
    }

    public static <T> Specification<T> isNullUuid(String attribute) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Path<UUID> path = getPath(root, attribute, UUID.class);
            return cb.isNull(path);
        };
    }

    public static <T> Specification<T> and(List<Specification<T>> specifications) {
        return specifications.stream().reduce(Specification::and).orElse(null);
    }

    public static Sort createSort(List<FilteredRequestDto.SortOrder> sortOrders) {
        Sort sort = Sort.unsorted();
        if (sortOrders != null && !sortOrders.isEmpty()) {
            for (FilteredRequestDto.SortOrder order : sortOrders) {
                Sort.Direction direction = (order.getDirection() == 1) ? Sort.Direction.ASC : Sort.Direction.DESC;
                sort = sort.and(Sort.by(direction, order.getFieldName()));
            }
        }
        return sort;
    }

    // Método para obtener la ruta del atributo
    private static <T, U> Path<U> getPath(Root<T> root, String attribute, Class<U> type) {
        String[] parts = attribute.split("\\.");
        Path<?> path = root; // Iniciamos con la raíz de la consulta

        for (int i = 0; i < parts.length; i++) {
            path = path.get(parts[i]); // Obtenemos cada parte del atributo
        }
        // Validamos que el tipo final sea compatible con el tipo deseado
        if (path.getJavaType().equals(type) || path.getJavaType().isAssignableFrom(type)) {
            return (Path<U>) path; // Casteamos a Path<U>
        } else {
            throw new IllegalArgumentException("El tipo del atributo '" + attribute + "' no coincide con el tipo esperado: " + type.getName());
        }
    }

}