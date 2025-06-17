package ru.movie.events;

import lombok.Data;

import java.time.Instant;

@Data
public class Event {
    private String id;
    private String type;
    private String timestamp;
    private Object payload;
}
