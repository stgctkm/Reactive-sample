package com.example.trial;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;

public class Trial {

    public static void main(String[] args) {
    }
}

class FirstStep {
    public static void main(String[] args) {
        Mono.just("abc").subscribe(System.out::println);
        System.out.println("xxx");
        Flux.just("a", "b", "c", "d", "e", "f").subscribe(System.out::println);
        System.out.println("zzz");
    }
}

class CreateDataOnFixedInterval {
    public static void main(String[] args) {
        // 何も出力されない
        Flux.interval(Duration.ofMillis(100))
                .map(it -> it + " " + LocalDateTime.now())
                .subscribe(System.out::println);
    }
}

class SecondStep {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        Flux.interval(Duration.ofMillis(100))
                .map(it -> it + " " + LocalDateTime.now())
                .log()
                .take(10)
                .log()
                .doOnComplete(countDownLatch::countDown)
                .log()
                .subscribe(System.out::println);

        countDownLatch.await();
    }
}