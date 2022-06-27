package com.example.account.Dto;

import com.example.account.AOP.AccountLockInterFace;
import com.example.account.type.TransactionResultType;
import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

public class CancelBalance {

    //Request
    //{
    //"userId":1,
    //"accountNumber":"1000000000",
    //"amount":1000
    //}

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Request implements AccountLockInterFace {

        @NotBlank
        private String transactionId;

        @NotBlank
        @Size(min = 10,max = 10)
        private String accountNumber;

        @NotNull
        @Min(0)
        @Max(1000_000_000)
        private Long amount;// 초기금액

    }

    //Response
    //{
    //"accountNumber":"1234567890",
    //"transactionResult":"S",
    //"transactionId":"c2033bb6d82a4250aecf8e27c49b63f6",
    //"amount":1000,
    //"transactedAt":"2022-06-01T23:26:14.671859"
    //}

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder

    public static class Response{
        private String accountNumber;
        private TransactionResultType transactionResult;
        private String transactionId;
        private Long amount;
        private LocalDateTime transactedAt;


        public static Response from(TransactionDto transactionDto){
            return Response.builder()
                    .accountNumber(transactionDto.getAccountNumber())
                    .transactionResult(transactionDto.getTransactionResultType())
                    .transactionId(transactionDto.getTransactionId())
                    .amount(transactionDto.getAmount())
                    .transactedAt(transactionDto.getTransactedAt())
                    .build();
        }
    }
}
