package com.example.account.service;

import com.example.account.Dto.TransactionDto;
import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.domain.Transaction;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.AccountUserRepository;
import com.example.account.repository.TransactionRepository;
import com.example.account.type.AccountStatus;
import com.example.account.type.ErrorCode;
import com.example.account.type.TransactionResultType;
import com.example.account.type.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.example.account.type.AccountStatus.*;
import static com.example.account.type.TransactionResultType.*;
import static com.example.account.type.TransactionType.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountUserRepository accountUserRepository;

    @InjectMocks
    private  TransactionService transactionService;


    @Test
    void successUseBalance(){
        //given
        AccountUser user = AccountUser.builder()
                .name("Pobi")
                .build();
        user.setId(12L);

        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(IN_USE)
                .accountNumber("1000000001")
                .balance(10000L)
                .build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(user)
                        .accountNumber("1000000000")
                        .accountStatus(IN_USE)
                        .balance(10000L)
                        .build()));

        given(transactionRepository.save(any()))
                .willReturn(Transaction.builder()
                        .account(account)
                        .transactionType(USE)
                        .transactionResultType(S)
                        .transactionId("transactionId")
                        .amount(1000L)
                        .transactedAt(LocalDateTime.now())
                        .balanceSnapshot(9000L)
                        .build());

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        //when
        TransactionDto transactionDto =  transactionService.useBalance(1L,"1000000000", 200L);

        //then
        verify(transactionRepository, times(1)).save(captor.capture());
        assertEquals(200L, captor.getValue().getAmount());
        assertEquals(9800L, captor.getValue().getBalanceSnapshot());
        assertEquals(transactionDto.getTransactionResultType(), S);
        assertEquals(transactionDto.getTransactionType(), USE);
        assertEquals(9000L, transactionDto.getBalanceSnapshot());
        assertEquals(1000L, transactionDto.getAmount());

    }

    @Test
    @DisplayName("?????? ?????? ?????? - ?????? ?????? ??????")
    void deleteAccount_UserNotFound(){
        AccountUser user = AccountUser.builder()
                .name("Pobi")
                .build();
        user.setId(12L);

        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        //when
        AccountException exception = assertThrows(AccountException.class,
                ()-> transactionService.useBalance(1L, "1234567890", 100L));

        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("?????? ?????? ?????? - ?????? ?????? ??????")
    void deleteAccount_Not_Account(){

        AccountUser user = AccountUser.builder()
                .name("Pobi")
                .build();
        user.setId(12L);

        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());

        //when
        AccountException exception = assertThrows(AccountException.class,
                ()-> transactionService.useBalance(1L, "1234567890", 100L));

        //then
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUNT, exception.getErrorCode());
    }

    @Test
    @DisplayName("?????? ????????? ?????? - ??????????????????")
    void deleteAccount_userUnMatch(){
        AccountUser user = AccountUser.builder()
                .name("Pobi")
                .build();
        user.setId(12L);


        AccountUser otherUser = AccountUser.builder()
                .name("tobi")
                .build();

        otherUser.setId(13L);
        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(otherUser)
                        .balance(0L)
                        .accountNumber("1234567890")
                        .build()));

        //when
        AccountException exception = assertThrows(AccountException.class,
                ()-> transactionService.useBalance(1L, "1234567890", 100L));

        //then
        assertEquals(ErrorCode.USER_ACCOUNT_UNMATCH, exception.getErrorCode());
    }

    @Test
    @DisplayName("????????? ????????? ??????????????? ?????????.")
    void deleteAccount_notEmpty(){
        //given
        AccountUser user = AccountUser.builder()
                .name("Pobi")
                .build();
        user.setId(12L);

        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(IN_USE)
                .accountNumber("1000000001")
                .balance(100L)
                .build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));

       //when
        AccountException exception = assertThrows(AccountException.class,
                ()-> transactionService.useBalance(1L, "1234567890",200L));
        //then
        assertEquals(ErrorCode.AMOUNT_EXCED_BALANCE, exception.getErrorCode());
        verify(transactionRepository, times(0)).save(any());


    }

    @Test
    @DisplayName("?????? ?????? ????????? ?????? X. - ?????? ?????? ??????")
    void deleteAccount_alreadyUnregistered(){
        AccountUser user = AccountUser.builder()
                .name("Pobi")
                .build();
        user.setId(12L);

        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(user)
                        .balance(100L)
                        .accountStatus(UNREGISTERED)
                        .accountNumber("1234567890")
                        .build()));

        //when
        AccountException exception = assertThrows(AccountException.class,
                ()-> transactionService.useBalance(1L, "1234567890", 100L));

        //then
        assertEquals(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED, exception.getErrorCode());
    }

    @Test
    @DisplayName("?????? ???????????? ?????? ??????")
    void saveFiledUseTransaction(){
        //given
        AccountUser user = AccountUser.builder()
                .name("Pobi")
                .build();
        user.setId(12L);

        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(IN_USE)
                .accountNumber("1000000001")
                .balance(10000L)
                .build();

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));

        given(transactionRepository.save(any()))
                .willReturn(Transaction.builder()
                        .account(account)
                        .transactionType(USE)
                        .transactionResultType(S)
                        .transactionId("transactionId")
                        .amount(1000L)
                        .transactedAt(LocalDateTime.now())
                        .balanceSnapshot(9000L)
                        .build());

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        //when
        transactionService.saveFiledUseTransaction("1000000000", 200L);

        //then
        verify(transactionRepository, times(1)).save(captor.capture());
        assertEquals(200L, captor.getValue().getAmount());
        assertEquals(10000L, captor.getValue().getBalanceSnapshot());
        assertEquals(captor.getValue().getTransactionResultType(), F);
    }

    @Test
    void successCancelBalance(){
        //given
        AccountUser user = AccountUser.builder()
                .name("Pobi")
                .build();
        user.setId(12L);

        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(IN_USE)
                .accountNumber("100000001")
                .balance(10000L)
                .build();

        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(USE)
                .transactionResultType(S)
                .transactionId("transactionId")
                .amount(200L)
                .transactedAt(LocalDateTime.now())
                .balanceSnapshot(9000L)
                .build();



        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(user)
                        .accountNumber("1000000000")
                        .accountStatus(IN_USE)
                        .balance(10000L)
                        .build()));

        given(transactionRepository.save(any()))
                .willReturn(Transaction.builder()
                        .account(account)
                        .transactionType(CANCEL)
                        .transactionResultType(S)
                        .transactionId("transactionIdForCancel")
                        .amount(200L)
                        .transactedAt(LocalDateTime.now())
                        .balanceSnapshot(10000L)
                        .build());

        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        //when
        TransactionDto transactionDto =  transactionService.cancelBalance("transactionId","1000000000", 200L);

        //then
        verify(transactionRepository, times(1)).save(captor.capture());
        assertEquals(200L, captor.getValue().getAmount());
        assertEquals(10000L + 200L, captor.getValue().getBalanceSnapshot());
        assertEquals(transactionDto.getTransactionResultType(), S);
        assertEquals(transactionDto.getTransactionType(), CANCEL);
        assertEquals(10000L, transactionDto.getBalanceSnapshot());
        assertEquals(200L, transactionDto.getAmount());

    }

    @Test
    @DisplayName("?????? ?????? ?????? - ?????? ?????? ?????? ??????")
    void cancelAccount_Not_Account(){

        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(Transaction.builder()
                        .build()));

        //when
        AccountException exception = assertThrows(AccountException.class,
                ()-> transactionService.cancelBalance("transactionId", "1234567890", 100L));

        //then
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUNT, exception.getErrorCode());
    }

    @Test
    @DisplayName("?????? ?????? ?????? - ?????? ?????? ?????? ??????")
    void cancelAccount_Not_transaction(){

        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.empty());

        //when
        AccountException exception = assertThrows(AccountException.class,
                ()-> transactionService.cancelBalance("transactionId", "1234567890", 100L));

        //then
        assertEquals(ErrorCode.Transaction_IS_NOT_Find, exception.getErrorCode());
    }

    @Test
    @DisplayName("????????? ????????? ???????????? ?????? - ?????? ?????? ?????? ??????")
    void cancelTransaction_transaction(){

        AccountUser user = AccountUser.builder()
                .name("Pobi")
                .build();
        user.setId(12L);

        AccountUser user2 = AccountUser.builder()
                .name("Pobi")
                .build();
        user2.setId(13L);

        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(IN_USE)
                .accountNumber("100000001")
                .balance(10000L)
                .build();

        Account accountNotUse = Account.builder()
                .accountUser(user2)
                .accountStatus(IN_USE)
                .accountNumber("100000002")
                .balance(10000L)
                .build();

        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(USE)
                .transactionResultType(S)
                .transactionId("transactionId")
                .amount(200L)
                .transactedAt(LocalDateTime.now())
                .balanceSnapshot(9000L)
                .build();

        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(accountNotUse));


        //when
        AccountException exception = assertThrows(AccountException.class,
                ()-> transactionService.cancelBalance("transactionId", "1234567890", 200L));

        //then
        assertEquals(ErrorCode.TRANSACTION_ACCOUNT_UN_MATCH, exception.getErrorCode());

    }

//    @Test
//    @DisplayName("??????????????? ??????????????? ???????????? ?????? - ?????? ?????? ?????? ??????")
//    void cancelMUST_FULLY_transaction(){
//
//        AccountUser user = AccountUser.builder()
//                .id(13L)
//                .name("Pobi")
//                .build();
//
//        AccountUser user2 = AccountUser.builder()
//                .id(13L)
//                .name("pabi")
//                .build();
//
//        Account account = Account.builder()
//                .accountUser(user)
//                .accountStatus(IN_USE)
//                .accountNumber("100000001")
//                .balance(10000L)
//                .build();
//
//        Account accountNotUse = Account.builder()
//                .accountUser(user2)
//                .accountStatus(IN_USE)
//                .accountNumber("100000002")
//                .balance(10000L)
//                .build();
//
//        Transaction transaction = Transaction.builder()
//                .account(account)
//                .transactionType(USE)
//                .transactionResultType(S)
//                .transactionId("transactionId")
//                .amount(200L)
//                .transactedAt(LocalDateTime.now())
//                .balanceSnapshot(9000L)
//                .build();
//
//        given(transactionRepository.findByTransactionId(anyString()))
//                .willReturn(Optional.of(transaction));
//
//        given(accountRepository.findByAccountNumber(anyString()))
//                .willReturn(Optional.of(accountNotUse));
//
//        //when
//        AccountException exception = assertThrows(AccountException.class,
//                ()-> transactionService.cancelBalance("transactionId", "1000000000", 200L));
//
//        //then
//        assertEquals(ErrorCode.CANCEL_MUST_FULLY, exception.getErrorCode());
//
//    }


    @Test
    void successQueryTransaction(){
        //given
        AccountUser user = AccountUser.builder()
                .name("Pobi")
                .build();
        user.setId(12L);

        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(IN_USE)
                .balance(10000L)
                .accountNumber("1000000012").build();

        account.setId(1L);
                Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(USE)
                .transactionResultType(S)
                .transactionId("transactionId")
                .amount(200L)
                .transactedAt(LocalDateTime.now())
                .balanceSnapshot(9000L)
                .build();

                given(transactionRepository.findByTransactionId(anyString()))
                        .willReturn(Optional.of(transaction));

                //when

                TransactionDto transactionDto = transactionService.queryTransactionId("trxId");

                //then
        assertEquals(USE,transactionDto.getTransactionType());
        assertEquals(S,transactionDto.getTransactionResultType());
        assertEquals(200L,transactionDto.getAmount());
        assertEquals("transactionId",transactionDto.getTransactionId());
    }

    @Test
    @DisplayName("???????????? ?????? - ?????? ?????? ??????")
    void queryTransaction_Transaction_Not_Found(){
        //given
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.empty());

        //when
        AccountException exception = assertThrows(AccountException.class,
                ()-> transactionService.queryTransactionId("transactionId"));

        //then
        assertEquals(ErrorCode.Transaction_IS_NOT_Find, exception.getErrorCode());
    }
}