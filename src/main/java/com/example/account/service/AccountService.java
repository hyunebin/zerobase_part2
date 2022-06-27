package com.example.account.service;

import com.example.account.Dto.AccountDto;
import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.AccountUserRepository;
import com.example.account.type.AccountStatus;
import com.example.account.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class AccountService {
    private final AccountRepository accountRepository; //생성자가 아니면 변경 불가능 Singleton
    private final AccountUserRepository accountUserRepository;

    /**
     *
     * @param userId
     * //사용자가 있는지 조회
     * @param initialBalance
     * //계좌의 번호를 생성하고
     * //계좌를 저장하고, 그 정보를 넘긴다.
     */

    @Transactional
    public AccountDto createAccount(Long userId, Long initialBalance) {
        // 유저가 존재하는지 확인
        AccountUser accountUser = getAccountUser(userId); // 존재하지 않는다면 해당 Exception 생성

        validateCreateAccount(accountUser);// 한명이 10개 이상의 계좌를 가질 수 없음

        //정렬을 해서 마지막꺼  + 1 없으면 기본값 1000000000넣어줌
        String newAccountNumber = accountRepository.findFirstByOrderByIdDesc()
                .map(account -> (Integer.parseInt(account.getAccountNumber())) + 1 + "")
                .orElse("1000000000");

        //이제 이렇게 해야 Account에 대한 정보가 accountRepository 를 통해 H2 DB에 저장된다.
        return AccountDto.fromEntity(accountRepository.save(
                Account.builder()
                        .accountUser(accountUser)
                        .accountStatus(AccountStatus.IN_USE)
                        .accountNumber(newAccountNumber)
                        .balance(initialBalance)
                        .registeredAt(LocalDateTime.now())
                        .build()
        ));
    }


    @Transactional
    public AccountDto deleteAccount(Long userId, String accountNumber) {
        //사용자 또는 계좌가 없는 경우
        AccountUser accountUser = getAccountUser(userId); // 존재하지 않는다면 해당 Exception 생성

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(()->new AccountException(ErrorCode.ACCOUNT_NOT_FOUNT));


        validateDeleteAccount(accountUser, account);

        //계좌를 해지했음으로 UNREGISTERED
        account.setAccountStatus(AccountStatus.UNREGISTERED);
        account.setUnRegisteredAt(LocalDateTime.now());


        accountRepository.save(account);

        return AccountDto.fromEntity(account);

    }

    private AccountUser getAccountUser(Long userId) {
        return accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));
    }


    @Transactional
    public List<AccountDto> getAccountByUserId(Long userId) {
        AccountUser accountUser = getAccountUser(userId);

        List<Account> accounts = accountRepository.findByAccountUser(accountUser);

        //List<Account> -> List<AccountDto>
        return accounts.stream()
                .map(AccountDto::fromEntity)
                .collect(Collectors.toList());
    }

    private void validateCreateAccount(AccountUser accountUser) {// 10개의 계좌를 가지는지 Check
        if(accountRepository.countByAccountUser(accountUser) == 10){
            throw new AccountException(ErrorCode.MAX_ACCOUNT_PER_USER_10);
        }
    }

    private void validateDeleteAccount(AccountUser accountUser, Account account){

        //사용자 아이디와 계좌 소유주가 다른 경우
        if(!Objects.equals(accountUser.getId(), account.getAccountUser().getId())){
            throw new AccountException(ErrorCode.USER_ACCOUNT_UNMATCH);
        };
        //계좌가 이미 해지 상태인 경우
        if(account.getAccountStatus() == AccountStatus.UNREGISTERED){
            throw new AccountException(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED);
        }

        //잔액이 있는 경우 실패 응답>
        if(account.getBalance() > 0){
            throw new AccountException(ErrorCode.BALANCE_IS_NOT_EMPTY);
        }
    }

    @Transactional
    public Account getAccount(Long id){
        return accountRepository.findById(id).get();
    }


}
