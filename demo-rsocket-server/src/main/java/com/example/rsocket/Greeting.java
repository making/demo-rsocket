package com.example.rsocket;

import java.util.StringJoiner;

public class Greeting {

    private final long id;

    private final String content;

    public Greeting(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Greeting.class.getSimpleName() + "[", "]")
            .add("id=" + id)
            .add("content='" + content + "'")
            .toString();
    }
}
