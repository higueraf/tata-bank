package com.tata.account.service;

import com.tata.account.dto.*;
import com.tata.account.entity.Account;
import com.tata.account.entity.Customer;
import com.tata.account.repository.AccountRepository;
import com.tata.account.repository.CustomerRepository;
import com.tata.account.security.SecurityUtil;
import com.tata.account.shared.GenericSpecification;
import com.tata.account.shared.PageRequestFromOne;
import com.tata.account.shared.SpecificationUtil;
import io.github.cdimascio.dotenv.Dotenv;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Dotenv dotenv;

    private final String CUSTOMER_SERVICE_URL;

    @Autowired
    public AccountService(Dotenv dotenv) {
        // Inicializamos CUSTOMER_SERVICE_URL usando el valor del .env
        this.CUSTOMER_SERVICE_URL = dotenv.get("URL_BANK_SERVICE") + "/api/customers/";
    }


    public AccountResponseDto createAccount(AccountRequestDto accountDto) {
        UUID createdBy = UUID.fromString(SecurityUtil.getDataSession().getUserId());
        String token = SecurityUtil.getDataSession().getToken();
        System.out.println(this.CUSTOMER_SERVICE_URL);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<ApiResponseDto<CustomerResponseDto>> response = restTemplate.exchange(
                    this.CUSTOMER_SERVICE_URL + accountDto.getCustomerId(),
                    HttpMethod.GET,
                    requestEntity,
                    (Class<ApiResponseDto<CustomerResponseDto>>)(Class<?>)ApiResponseDto.class
            );
            ApiResponseDto<CustomerResponseDto> apiResponse = response.getBody();
            if (apiResponse == null || !apiResponse.isSuccess() || apiResponse.getData() == null) {
                throw new IllegalArgumentException("Customer not found or invalid response from Customer Service.");
            }
            Customer customer = modelMapper.map(apiResponse.getData(), Customer.class);
            Account account = modelMapper.map(accountDto, Account.class);
            account.setCustomer(customer);
            account.setCreatedBy(createdBy);
            account.setCreatedAt(LocalDateTime.now());
            Account savedAccount = accountRepository.save(account);
            return modelMapper.map(savedAccount, AccountResponseDto.class);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Account number already exists.", e);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error creating account: " + e.getMessage(), e);
        }
    }


    public AccountResponseDto updateAccount(UUID id, AccountRequestDto accountDto) {
        UUID updatedBy = UUID.fromString(SecurityUtil.getDataSession().getUserId());

        try {
            Account existingAccount = accountRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Account not found"));

            modelMapper.map(accountDto, existingAccount);
            existingAccount.setCustomer(
                    customerRepository.findById(accountDto.getCustomerId())
                            .orElseThrow(() -> new IllegalArgumentException("Customer not found"))
            );
            existingAccount.setId(id);
            existingAccount.setUpdatedBy(updatedBy);
            existingAccount.setUpdatedAt(LocalDateTime.now());

            Account updatedAccount = accountRepository.save(existingAccount);
            return modelMapper.map(updatedAccount, AccountResponseDto.class);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Account number already exists.", e);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error updating account: " + e.getMessage(), e);
        }
    }

    public List<AccountResponseDto> getAllAccounts() {
        try {
            return accountRepository.findAllByDeletedAtIsNull().stream()
                    .map(account -> modelMapper.map(account, AccountResponseDto.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new IllegalArgumentException("Error fetching accounts: " + e.getMessage(), e);
        }
    }

    public Optional<AccountResponseDto> getAccountById(UUID id) {
        try {
            return accountRepository.findByIdAndDeletedAtIsNull(id)
                    .map(account -> modelMapper.map(account, AccountResponseDto.class));
        } catch (Exception e) {
            throw new IllegalArgumentException("Error fetching account by ID: " + e.getMessage(), e);
        }
    }

    public Page<AccountResponseDto> getFilteredAccounts(FilteredRequestDto filteredRequestDto) {
        try {
            Specification<Account> accountSpec = new GenericSpecification<>(Account.class)
                    .getSpecification(filteredRequestDto);
            Sort sort = SpecificationUtil.createSort(filteredRequestDto.getSortOrders());
            Pageable pageable = PageRequestFromOne.of(
                    filteredRequestDto.getPage(),
                    filteredRequestDto.getPageSize(),
                    sort
            );

            Page<Account> accounts = accountRepository.findAll(accountSpec, pageable);
            return accounts.map(account -> modelMapper.map(account, AccountResponseDto.class));
        } catch (Exception e) {
            throw new IllegalArgumentException("Error fetching filtered accounts: " + e.getMessage(), e);
        }
    }

    public void deleteAccount(UUID id) {
        UUID deletedBy = UUID.fromString(SecurityUtil.getDataSession().getUserId());

        try {
            Account account = accountRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Account not found"));

            account.setDeletedBy(deletedBy);
            account.setDeletedAt(LocalDateTime.now());

            accountRepository.save(account);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error deleting account: " + e.getMessage(), e);
        }
    }
}
