package com.cosmetics.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig implements AsyncConfigurer {

    private int CORE_POOL_SIZE = 10;
    private int MAX_POOL_SIZE = 50;
    private int QUEUE_CAPACITY = 1000;

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CORE_POOL_SIZE);       // 항상 유지할 최소 스레드 수
        executor.setMaxPoolSize(MAX_POOL_SIZE);        // 생성할 수 있는 최대 스레드 수
        executor.setQueueCapacity(QUEUE_CAPACITY);     // 대기 큐의 최대 크기
        executor.setThreadNamePrefix("Cosmetics-Thread-Pool");  // 생성된 스레드 이름 접두사
        executor.initialize();              // 스레드 풀 초기화
        log.error("{}======================", Thread.currentThread().getName());
        return executor;
        //상황에 따라 최소 스레드 수 최대 스레드 수 대기 큐의 크기를 조절해야하는데 그 상황이 어떤 상황에 따라 어떤 걸 보고 어떻게 지정해야할 지
        //궁금합니다
    }
}
