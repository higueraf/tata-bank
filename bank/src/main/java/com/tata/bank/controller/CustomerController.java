package com.tata.bank.controller;

import com.tata.bank.dto.*;
import com.tata.bank.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping
    public ResponseEntity<ApiResponseDto<CustomerResponseDto>> createCustomer(
            @RequestBody CustomerRequestDto customerDto) {
        CustomerResponseDto createdCustomer = customerService.createCustomer(customerDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDto<>(
                        true,
                        "Customer created successfully",
                        createdCustomer));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<CustomerResponseDto>> updateCustomer(
            @PathVariable UUID id,
            @RequestBody CustomerRequestDto customerDto) {
        CustomerResponseDto updatedCustomer = customerService.updateCustomer(id, customerDto);
        return ResponseEntity.ok(
                new ApiResponseDto<>(
                        true,
                        "Customer updated successfully",
                        updatedCustomer));
    }

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<CustomerResponseDto>>> getAllCustomers() {
        List<CustomerResponseDto> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(
                new ApiResponseDto<>(
                        true,
                        "Customers retrieved successfully",
                        customers));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<CustomerResponseDto>> getCustomerById(@PathVariable UUID id) {
        Optional<CustomerResponseDto> customer = customerService.getCustomerById(id);

        return customer
                .map(value -> ResponseEntity.ok(new ApiResponseDto<>(
                        true,
                        "Customer retrieved successfully",
                        value)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponseDto<>(
                                false,
                                "Customer not found",
                                null)));
    }


    @PostMapping("/filtered-page")
    public ResponseEntity<ApiResponseDto<PaginationResponseDto<CustomerResponseDto>>> getFilteredUsers(
            @RequestBody FilteredRequestDto filteredRequestDto) {
        Page<CustomerResponseDto> customersDto = customerService.getFilteredCustomers(filteredRequestDto);
        PaginationResponseDto<CustomerResponseDto> paginationResponseDto = new PaginationResponseDto<>(
                customersDto.getContent(),
                customersDto.getTotalElements(),
                customersDto.getNumber(),
                customersDto.getSize(),
                customersDto.getTotalPages()
        );
        return ResponseEntity.ok(
                new ApiResponseDto<>(
                        true,
                        "Customers retrieved successfully",
                        paginationResponseDto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteCustomer(@PathVariable UUID id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok(
                new ApiResponseDto<>(
                        true,
                        "Customer deleted successfully",
                        null));
    }
}
