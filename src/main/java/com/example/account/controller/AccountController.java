package com.example.account.controller;

import com.example.account.Dto.AccountDto;
import com.example.account.Dto.CreateAccount;
import com.example.account.Dto.DeleteAccount;
import com.example.account.domain.Account;
import com.example.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;


    @PostMapping("/account")
    public CreateAccount.Response createAccount(@RequestBody @Valid CreateAccount.Request request){ // Vaild는 RequestBody의 검증
        AccountDto accountDto = accountService
                .createAccount(request.getUserId(), request.getInitialBalance());
        return CreateAccount.Response.from(accountDto);
    }

    @DeleteMapping("/accountDelete")
    public DeleteAccount.Response deleteAccount(@RequestBody @Valid DeleteAccount.Request request){ // Vaild는 RequestBody의 검증
        AccountDto accountDto = accountService
                .deleteAccount(request.getUserId(), request.getAccountNumber());
        return DeleteAccount.Response.from(accountDto);
    }


    @GetMapping("/account")
    public List<AccountInfo> getAccountsByUserId(@RequestParam("user_id") Long userId){
        return  accountService.getAccountByUserId(userId)
                .stream().map(accountDto -> AccountInfo.builder()
                        .accountNumber(accountDto.getAccountNumber())
                        .balance(accountDto.getBalance())
                        .build())
                .collect(Collectors.toList());
    }

    @GetMapping("/account/{id}")
    public Account getAccount(@PathVariable Long id){ // PathVariable은 값이 같다면 생략가능
        return accountService.getAccount(id);
    }


}
