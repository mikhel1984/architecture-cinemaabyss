package ru.local.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserEvent {

  @JsonProperty(value = "user_id", required = true)
  private Integer userId;
  private String username;
  private String email;
  @JsonProperty(required = true)
  private String action;
  @JsonProperty(required = true)
  private String timestamp;

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  @Override
  public String toString() {
    return "UserEvent{" +
        "userId=" + userId +
        ", username='" + username + '\'' +
        ", email='" + email + '\'' +
        ", action='" + action + '\'' +
        ", timestamp='" + timestamp + '\'' +
        '}';
  }
}
