package ru.movie.events;

import lombok.Data;

@Data
public class EventResponse {
    private final String status;
    private final Integer partition;
    private final Long offset;
    private final Event event;
}
