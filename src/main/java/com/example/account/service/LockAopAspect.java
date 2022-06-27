package com.example.account.service;


import com.example.account.AOP.AccountLockInterFace;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LockAopAspect {
    private final LockService lockService;
    @Around("@annotation(com.example.account.AOP.accountLock) && args(request)")
    public Object aroundMethod(ProceedingJoinPoint proceedingJoinPoint, AccountLockInterFace request) throws Throwable {

        lockService.Lock(request.getAccountNumber());
        try {
            return proceedingJoinPoint.proceed();
        }finally {
            //lock 해체
            lockService.unLock(request.getAccountNumber());
        }
    }
}
