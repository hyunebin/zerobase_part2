package com.example.account.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INTERNAL_SERVER_ERROR("서버 오류가 발생했습니다."),
    USER_NOT_FOUND("사용자가 없습니다."),
    INVALID_ERROR("잘못된 요청입니다."),
    MAX_ACCOUNT_PER_USER_10("사용자 최대 계좌는 10개."),
    ACCOUNT_NOT_FOUNT("계좌번호가 없습니다."),
    USER_ACCOUNT_UNMATCH("사용자와 계좌의 소유주가 다릅니다."),
    ACCOUNT_ALREADY_UNREGISTERED("계좌가 이미 해지되었습니다."),
    BALANCE_IS_NOT_EMPTY("잔액이 있는 계좌는 해지할 수 없습니다."),
    Transaction_IS_NOT_Find("트랜잭션이 없습니다."),
    TRANSACTION_ACCOUNT_UN_MATCH("이 거래는 해당 계좌에서 발생한 거래가 아님"),
    CANCEL_MUST_FULLY("부분 취소는 허용되지 않습니다."),
    AMOUNT_EXCED_BALANCE("사용 금액이 잔액보다 더 크다."),
    TOO_OLD_ORDER_TO_CANCEL("1년이 지난 거래는 취소가 불가능합니다."), ACCOUNT_TRANSACTION_LOCK("해당 계좌는 사용중");


    private final String description;
}
