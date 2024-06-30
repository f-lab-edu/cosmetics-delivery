package com.cosmetics.domain.sms.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SmsService {

    @Async
    public void smsMessage(Long goodsNo) {
        //sms 보낸다고 가정
        //async와 CompletableFuture를 혼합해서 사용하는 경우도 있던데 그런 경우는 어떠한 경우일까요?
        log.error("GoodsNo '{}' Create Success. SMS SEND Success, {}", goodsNo, Thread.currentThread().getName());
    }
}
