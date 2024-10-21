package com.tata.bank.repository;

import com.tata.bank.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    User findByUsername(String Username);

    Optional<User> findFirstByUsernameAndDeletedByIsNull(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndDeletedByIsNull(String email);

}
