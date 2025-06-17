package ru.movie.events;

import lombok.Data;

@Data
public class UserEvent {
    private Integer user_id;
    private String username;
    private String email;
    private String action; // registered, logged_in, updated_profile, etc.
    private String timestamp;
}
