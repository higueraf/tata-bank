package com.tata.account.repository;

import com.tata.account.entity.Account;
import com.tata.account.entity.AccountTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountTransactionRepository
        extends JpaRepository<AccountTransaction, UUID>, JpaSpecificationExecutor<AccountTransaction> {

    // Consultar transacciones por ID de cuenta y no eliminadas
    List<AccountTransaction> findByAccountIdAndDeletedAtIsNull(UUID accountId);

    // Consultar una transacción específica por ID y no eliminada
    Optional<AccountTransaction> findByIdAndDeletedAtIsNull(UUID id);

    // Obtener la última transacción por cuenta
    Optional<AccountTransaction> findTopByAccountOrderByCreatedAtDesc(Account account);

    // Consultar transacciones por ID de cuenta y rango de fechas
    List<AccountTransaction> findByAccountIdAndDateBetweenAndDeletedAtIsNull(
            UUID accountId, LocalDate startDate, LocalDate endDate);



    List<AccountTransaction> findByAccountCustomerIdAndDateBetweenAndDeletedAtIsNull(
            UUID customerId, LocalDate startDate, LocalDate endDate);

    // Consultar transacciones por rango de fechas y no eliminadas
    List<AccountTransaction> findByDateBetweenAndDeletedAtIsNull(
            LocalDate startDate, LocalDate endDate);
}
