package com.tata.bank.service;

import com.tata.bank.dto.CustomerRequestDto;
import com.tata.bank.dto.CustomerResponseDto;
import com.tata.bank.dto.FilteredRequestDto;
import com.tata.bank.entity.Customer;
import com.tata.bank.repository.CustomerRepository;
import com.tata.bank.security.SecurityUtil;
import com.tata.bank.shared.GenericSpecification;
import com.tata.bank.shared.PageRequestFromOne;
import com.tata.bank.shared.SpecificationUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public CustomerResponseDto createCustomer(CustomerRequestDto customerDto) {
        UUID createdBy = UUID.fromString(SecurityUtil.getDataSession().getUserId());

        try {
            Customer customer = modelMapper.map(customerDto, Customer.class);
            customer.setPassword(passwordEncoder.encode(customerDto.getPassword()));
            customer.setCreatedBy(createdBy);
            customer.setCreatedAt(LocalDateTime.now());

            Customer savedCustomer = customerRepository.save(customer);
            return modelMapper.map(savedCustomer, CustomerResponseDto.class);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Identification already exists.", e);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error creating customer: " + e.getMessage(), e);
        }
    }

    public CustomerResponseDto updateCustomer(UUID id, CustomerRequestDto customerDto) {
        UUID updatedBy = UUID.fromString(SecurityUtil.getDataSession().getUserId());

        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        try {
            modelMapper.map(customerDto, existingCustomer);
            existingCustomer.setPassword(passwordEncoder.encode(customerDto.getPassword()));
            existingCustomer.setUpdatedBy(updatedBy);
            existingCustomer.setUpdatedAt(LocalDateTime.now());

            Customer updatedCustomer = customerRepository.save(existingCustomer);
            return modelMapper.map(updatedCustomer, CustomerResponseDto.class);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Identification already exists.", e);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error updating customer: " + e.getMessage(), e);
        }
    }

    public List<CustomerResponseDto> getAllCustomers() {
        try {
            return customerRepository.findAllByDeletedAtIsNull().stream()
                    .map(customer -> modelMapper.map(customer, CustomerResponseDto.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new IllegalArgumentException("Error fetching customers: " + e.getMessage(), e);
        }
    }

    public Optional<CustomerResponseDto> getCustomerById(UUID id) {
        try {
            return customerRepository.findByIdAndDeletedAtIsNull(id)
                    .map(customer -> modelMapper.map(customer, CustomerResponseDto.class));
        } catch (Exception e) {
            throw new IllegalArgumentException("Error fetching customer by ID: " + e.getMessage(), e);
        }
    }

    public Page<CustomerResponseDto> getFilteredCustomers(FilteredRequestDto filteredRequestDto) {
        try {
            Specification<Customer> customerSpec = new GenericSpecification<>(Customer.class)
                    .getSpecification(filteredRequestDto);
            Sort sort = SpecificationUtil.createSort(filteredRequestDto.getSortOrders());
            Pageable pageable = PageRequestFromOne.of(
                    filteredRequestDto.getPage(),
                    filteredRequestDto.getPageSize(),
                    sort
            );

            Page<Customer> customers = customerRepository.findAll(customerSpec, pageable);
            return customers.map(customer -> modelMapper.map(customer, CustomerResponseDto.class));
        } catch (Exception e) {
            throw new IllegalArgumentException("Error fetching filtered customers: " + e.getMessage(), e);
        }
    }

    public void deleteCustomer(UUID id) {
        UUID deletedBy = UUID.fromString(SecurityUtil.getDataSession().getUserId());

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        try {
            customer.setDeletedBy(deletedBy);
            customer.setDeletedAt(LocalDateTime.now());
            customerRepository.save(customer);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error deleting customer: " + e.getMessage(), e);
        }
    }
}
