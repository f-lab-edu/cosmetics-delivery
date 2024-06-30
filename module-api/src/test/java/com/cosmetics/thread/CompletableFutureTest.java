package com.cosmetics.thread;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * 더이상 ExecutorService를 쓰지않아도됨
 * */
public class CompletableFutureTest {
    /**
     * 비동기 작업 실행
     * runAsync
     * <p>
     * 반환값이 없는 경우
     * 비동기로 작업 실행 콜백
     */
    @Test
    public void testRunAsync() throws ExecutionException, InterruptedException {
        //Future에서 비동기 작업실행 , 작업 콜백 , 작업 조합(여러 퓨처 조합) ex) 회원 정보를 가져오고, 알림을 발송하는 등,  예외처리

        //Runnable runnable = () -> System.out.println("Hello");
        //CompletableFuture<Void> future = CompletableFuture.runAsync(runnable);

        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> System.out.println(Thread.currentThread().getName()));
        System.out.println(future.get());
        System.out.println("end " + Thread.currentThread().getName());
    }

    /**
     * 비동기 작업 실행
     * supplyAsync
     * 반환값이 있는 경우
     * 비동기로 작업 실행 콜
     **/
    @Test
    public void testSupplyAsync() throws ExecutionException, InterruptedException {
        //Future에서 비동기 작업실행 , 작업 콜백 , 작업 조합(여러 퓨처 조합) ex) 회원 정보를 가져오고, 알림을 발송하는 등,  예외처리
        //ForkJoinPool 풀을 씀
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            return Thread.currentThread().getName();
        });

        System.out.println(future.get());
        System.out.println("end " + Thread.currentThread().getName());
    }

    @Test
    public void testExecuteServicePoolSupplyAsync() throws ExecutionException, InterruptedException {
        //Future에서 비동기 작업실행 , 작업 콜백 , 작업 조합(여러 퓨처 조합) ex) 회원 정보를 가져오고, 알림을 발송하는 등,  예외처리
        //ForkJoinPool.commonPool() 말고 원하는 풀
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            return Thread.currentThread().getName();
        }, executorService);

        System.out.println(future.get());
        System.out.println("end " + Thread.currentThread().getName());
    }

    /**
     * 작업콜백 thenApply
     * 반환 값을 받아서 다른 값을 반환함
     * 함수형 인터페이스 Function을 파라미터로 받음
     */
    @Test
    public void thenApply() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                    return Thread.currentThread().getName();
                })
                .thenApply(name -> {
                    return "1" + name;
                })
                .thenApply(name -> {
                    return "2" + name;
                })
                .thenApply(name -> {
                    return "3" + name;
                });
        System.out.println("future.get() : " + future.get());
        System.out.println("end " + Thread.currentThread().getName());
    }

    /**
     * 작업콜백 thenApply
     * 반환 값을 받아서 다른 값을 반환함
     * 함수형 인터페이스 Function을 파라미터로 받음
     */
    @Test
    public void thenApplyAsync2() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> Thread.currentThread().getName())
                .thenApplyAsync(name -> {
                    try {
                        Thread.sleep(5000L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println(name);
                    return " 1 " + name + "1end";
                }).thenApplyAsync(name -> {
                    try {
                        Thread.sleep(3000L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println(name);
                    return " 2 " + name + "2end";
                }).thenApplyAsync(name -> {
                    try {
                        Thread.sleep(4000L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println(name);
                    return " 3 " + name + "3end";
                }).thenApplyAsync(String::toUpperCase);

//        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> Thread.currentThread().getName())
//                .thenApplyAsync(name -> name + " " + Thread.currentThread().getName() + "1end ")
//                .thenApplyAsync(name -> name + " " + Thread.currentThread().getName() + "2end ")
//                .thenApplyAsync(name -> name + " " + Thread.currentThread().getName() + "3end");
        //.thenApply(String::toUpperCase);

        System.out.println("future.get() : " + future.get());

    }


    /**
     * 작업콜백 thenApply
     * 반환 값을 받아서 처리하고 값을 반환하지 않음
     * 함수형 인터페이스 Customer를 파라미터로 받음
     */
    @Test
    public void thenAccept() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
            return Thread.currentThread().getName();
        }).thenAccept(name -> {
            System.out.println(name.toUpperCase());
        });

        System.out.println(future.get());
    }

    /**
     * 작업콜백 thenRun
     * 반환 값을 파라미터로 받지 않고 다른 작업
     */
    @Test
    public void thenRun() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
            return Thread.currentThread().getName();
        }).thenRun(() -> {
            System.out.println(Thread.currentThread().getName());
        });

        System.out.println(future.get());
    }

    /**
     * 작업조합
     * thenCompose
     * 두 작업을 이어서 실행하도록 조합, 앞선 작업의 결과를 받아서 이어서 한다.
     * 함수형 인터페이스 Function을 파라미터로
     */
    @Test
    public void thenCompose() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            return "future1 : " + Thread.currentThread().getName();
        });
        //future1 먼저실행 -> callFuture2 이어서 실행
        CompletableFuture<String> future3 = future1.thenCompose((message) -> callFuture2(message));
        System.out.println(future3.get());

    }

    public CompletableFuture<String> callFuture2(String message) {
        return CompletableFuture.supplyAsync(() -> {
            return message + ", future2 : " + Thread.currentThread().getName();
        });
    }

    /**
     * 작업조합
     * thenCombine
     * 두 작업이 독립적으로 실행
     */
    @Test
    public void thenCombine() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            return "future1 : " + Thread.currentThread().getName();
        });
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            return "future2 : " + Thread.currentThread().getName();
        });

        CompletableFuture<String> future3 = future1.thenCombine(future2, (result1, result2) -> {
            return result1 + "," + result2;
        });
        System.out.println(future3.get());
    }

    @Test
    public void allOf() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "future1 : " + Thread.currentThread().getName();
            })
        .exceptionally(e -> {
            System.out.println("Exception " + e.getMessage());
            return e.getMessage();
        })
        .handle((result, e) -> { //(결과값, 에러)를 반환받아 에러가 발생한 경우와 아닌 경우 모두를 처리할 수 있음
            System.out.println("handle " + result);
            return e == null
                    ? result
                    : e.getMessage();
        });

        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            return " future2 : " + Thread.currentThread().getName();
        });

        CompletableFuture<Void> result = CompletableFuture.allOf(future1, future2)
                .thenAccept(v -> {
                    try {
                        String strFuture1 = future1.get();
                        String strFuture2 = future2.get();
                        System.out.println("result1 = " + strFuture1 + strFuture2);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                });
        System.out.println("111111" + Thread.currentThread().getName());
        List<CompletableFuture<String>> futures = Arrays.asList(future1, future2);
        CompletableFuture<Void> result2 = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]))
                .thenAccept(v -> {
                    futures.stream()
                            .map(CompletableFuture::join)
                            .forEach(System.out::println);
                });
        System.out.println("222222" + Thread.currentThread().getName());
        System.out.println(result2.join());
    }

    @Test
    public void anyOf() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "future1 : " + Thread.currentThread().getName();
        });

        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            return "future2 : " + Thread.currentThread().getName();
        });

        CompletableFuture<Void> result = CompletableFuture.anyOf(future1, future2)
                .thenAccept(System.out::println);
        //System.out.println(result.get());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void handle(boolean doThrow) throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            if (doThrow) {
                throw new IllegalArgumentException("Invalid Argument");
            }

            return "Thread: " + Thread.currentThread().getName();
        }).handle((result, e) -> {
            return e == null
                    ? result
                    : e.getMessage();
        });

        System.out.println(future.get());
    }
    /**
     * 비동기 처리는 get을 안사용하고 콜백을 통해 하는게 맞지!
     * */
    @Test
    public void thenApplyAsync() throws ExecutionException, InterruptedException {
        CompletableFuture.supplyAsync(()->
        {
            // 비동기 작업
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return "Hello";
        }).thenApply(result -> {
        // 비동기 작업의 결과를 이용한 후속 작업
        return result + " World";
        }).thenAccept(System.out::println); // 출력: Hello World

        // 메인 스레드는 블록킹되지 않고 계속 실행됨
        System.out.println("This will print immediately.");
    }
}
