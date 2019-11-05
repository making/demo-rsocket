package com.example.demobackpressure;

import com.example.demobackpressure.name.Name;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.BaseSubscriber;

import java.util.concurrent.CountDownLatch;

/**
 * https://github.com/bclozel/spring-flights/blob/master/demo-backpressure/src/main/java/io/spring/demo/backpressure/DemoBackpressureApplication.java
 */
public class DemoSubscriber extends BaseSubscriber<Name> {

    private volatile CountDownLatch latch = new CountDownLatch(0);

    private Logger log = LoggerFactory.getLogger(DemoSubscriber.class);

    @Override
    protected void hookOnSubscribe(Subscription subscription) {
        // Do not request any items to start...
    }

    @Override
    protected void hookOnNext(Name value) {
        log.info("[" + this.latch.getCount() + "] '" + value + "'");
        this.latch.countDown();
    }

    public void requestBatch(int n) {
        this.latch = new CountDownLatch(n);
        request(n);
    }

    public void awaitBatch() throws InterruptedException {
        this.latch.await();
    }
}
