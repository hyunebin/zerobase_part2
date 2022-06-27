package com.example.account.domain;


import com.example.account.exception.AccountException;
import com.example.account.type.AccountStatus;
import com.example.account.type.ErrorCode;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners({AuditingEntityListener.class})
public class Account extends BaseEntity{

    @ManyToOne
    private AccountUser accountUser;

    @Enumerated(EnumType.STRING) // ENUM의 원본으로 등록하면 무엇인지 모르기 때문에 String으로 설정
    private AccountStatus accountStatus;



    @CreatedDate
    private LocalDateTime registeredAt;

    @LastModifiedDate
    private LocalDateTime unRegisteredAt;

    private String accountNumber; // 계좌번호
    private Long balance;

    public void useBalance(Long amount){
        if(amount > balance){
            throw new AccountException(ErrorCode.AMOUNT_EXCED_BALANCE);
        }

        balance-=amount;
    }

    public void cancelBalance(Long amount) {
        if(amount < 0){
            throw new AccountException(ErrorCode.INVALID_ERROR);
        }

        balance+=amount;
    }
}
