package ru.movie.events;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MovieEvent {
    private Integer movie_id;
    private String title;
    private String action; // viewed, rated, added, etc.
    private Integer user_id;
    private Double rating;
    private List<String> genres;
    private String description;
}
