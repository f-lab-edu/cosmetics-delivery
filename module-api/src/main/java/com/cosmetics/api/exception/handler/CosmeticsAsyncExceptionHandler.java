package com.cosmetics.api.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;
@Slf4j
public class CosmeticsAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        log.error("AsyncExceptionHandler = {}", ex.getMessage());
        log.error("method = {}", method.getName());
        log.error("params = {}", params);
        log.error("Thread = {}", Thread.currentThread().getName());
    }
}
