package com.example.account.repository;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {// 사용할 Entity , 와 PK
    Optional<Account> findFirstByOrderByIdDesc();
    Optional<Account> findByAccountNumber(String accountNumber);
    Integer countByAccountUser(AccountUser accountUser);
    List<Account> findByAccountUser(AccountUser accountUser);

}
