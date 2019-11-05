package com.example.demobackpressure;

import com.example.demobackpressure.name.NameClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class BackpressureRunner implements ApplicationRunner {

    private final Logger log = LoggerFactory.getLogger(BackpressureRunner.class);

    private final NameClient nameClient;

    public BackpressureRunner(NameClient nameClient) {
        this.nameClient = nameClient;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        final DemoSubscriber subscriber = new DemoSubscriber();
        final AtomicLong counter = new AtomicLong(0);
        this.nameClient.names()
            .doOnRequest(n -> log.info("request({})", n))
            .doOnNext(__ -> counter.incrementAndGet())
            .doFinally(__ -> log.info("Consumed {}", counter))
            .subscribe(subscriber);

        for (Scanner sc = new Scanner(System.in); !subscriber.isDisposed(); subscriber.awaitBatch()) {
            System.out.print("\nEnter demand (int): ");
            try {
                int n = sc.nextInt();
                System.out.println();
                if (n < 0) {
                    subscriber.cancel();
                    break;
                }
                subscriber.requestBatch(n);
            } catch (Exception ex) {
                subscriber.cancel();
            }
        }
        Runtime.getRuntime().addShutdownHook(new Thread(subscriber::cancel));
    }
}
