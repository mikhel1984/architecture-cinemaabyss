package ru.movie.events;

import lombok.Data;

@Data
public class HealthResponse {
    private boolean status;

    public HealthResponse(boolean status) {
        this.status = status;
    }
}
