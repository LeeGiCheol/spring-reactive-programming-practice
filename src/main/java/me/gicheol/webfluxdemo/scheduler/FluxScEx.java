package me.gicheol.webfluxdemo.scheduler;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*
    JVM은 모든 Thread가 종료되고 Daemon Thread만 남는다면,
    강제로 종료 시킨다.


 */

@Slf4j
public class FluxScEx {

    public static void main(String[] args) throws InterruptedException {
        // Daemon Thread
//        Flux.interval(Duration.ofMillis(500))
//            .subscribe(s -> log.debug("onNext:{}", s));
//
//        log.debug("exit");
//        TimeUnit.SECONDS.sleep(5);

        Flux.interval(Duration.ofMillis(500))
                .take(10)
                .subscribe(s -> log.debug("onNext:{}", s));

        log.debug("exit");
        TimeUnit.SECONDS.sleep(5);



        // User Thread
//        Executors.newSingleThreadExecutor().execute(() -> {
//            try {
//                TimeUnit.SECONDS.sleep(2);
//            } catch (InterruptedException e) { }
//            System.out.println(Thread.currentThread().getName() + "hello");
//        });
//        System.out.println(Thread.currentThread().getName() + "exit");
    }

}
