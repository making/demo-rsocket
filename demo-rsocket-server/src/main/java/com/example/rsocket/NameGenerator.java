package com.example.rsocket;

import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class NameGenerator {

    private static final Logger log = LoggerFactory.getLogger(NameGenerator.class);

    private static final Faker faker = Faker.instance(Locale.JAPAN);

    public static Flux<Name> stream() {
        final AtomicLong counter = new AtomicLong(0);
        return Flux.<Name>create(sink -> {
            final AtomicBoolean canceled = new AtomicBoolean(false);
            final Disposable cancel = () -> canceled.set(true);
            sink.onCancel(cancel);
            sink.onDispose(cancel);
            sink.onRequest(n -> {
                for (int i = 0; i < n; i++) {
                    final com.github.javafaker.Name name = faker.name();
                    sink.next(new Name(name.firstName(), name.lastName()));
                    if (counter.incrementAndGet() % 1_000 == 0) {
                        log.info("Generating {} names...", counter);
                    }
                }
            });
        })
            .doOnCancel(() -> log.info("Canceled."))
            .doOnError(e -> log.warn("Error", e))
            .doOnRequest(n -> log.info("request({})", n))
            .doFinally(__ -> log.info("Generated {} names", counter.get()));
    }

}
