package com.tata.account.repository;

import com.tata.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface AccountRepository extends JpaRepository<Account, UUID>, JpaSpecificationExecutor<Account> {
    Optional<Account> findByAccountNumber(String accountNumber);
    Optional<Account> findByIdAndDeletedAtIsNull(UUID id);
    List<Account> findAllByDeletedAtIsNull();

}
