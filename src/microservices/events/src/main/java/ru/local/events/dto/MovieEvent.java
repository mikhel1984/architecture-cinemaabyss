package ru.local.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class MovieEvent {

  @JsonProperty(value = "movie_id", required = true)
  private Integer movieId;
  @JsonProperty(required = true)
  private String title;
  @JsonProperty(required = true)
  private String action;
  @JsonProperty(value = "user_id")
  private Integer userId;
  private double rating;
  private List<String> genres;
  private String description;

  public Integer getMovieId() {
    return movieId;
  }

  public void setMovieId(Integer movieId) {
    this.movieId = movieId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public double getRating() {
    return rating;
  }

  public void setRating(double rating) {
    this.rating = rating;
  }

  public List<String> getGenres() {
    return genres;
  }

  public void setGenres(List<String> genres) {
    this.genres = genres;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String toString() {
    return "MovieEvent{" +
        "movieId=" + movieId +
        ", title='" + title + '\'' +
        ", action='" + action + '\'' +
        ", userId=" + userId +
        ", rating=" + rating +
        ", genres=" + genres +
        ", description='" + description + '\'' +
        '}';
  }
}
