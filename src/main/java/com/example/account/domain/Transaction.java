package com.example.account.domain;


import com.example.account.type.TransactionResultType;
import com.example.account.type.TransactionType;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)


public class Transaction extends BaseEntity{


    @Enumerated(EnumType.STRING) // ENUM의 원본으로 등록하면 무엇인지 모르기 때문에 String으로 설정
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING) // ENUM의 원본으로 등록하면 무엇인지 모르기 때문에 String으로 설정
    private TransactionResultType transactionResultType;

    @ManyToOne//거래가 발생한 계좌(N:1 연결)
    private Account account;

    private Long amount;//금액
    private Long balanceSnapshot;//거래 후 계좌 잔액
    private String transactionId;//계좌 해지일시
    private LocalDateTime transactedAt;//거래일시

}
