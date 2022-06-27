package com.example.account.controller;


import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

//client와 application간의 대응
//다른 DTO는 Service와 Controller간
public class AccountInfo {
    private String accountNumber;
    private Long balance;

}
