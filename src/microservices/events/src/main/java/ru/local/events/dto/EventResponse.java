package ru.local.events.dto;

public class EventResponse {

  private String status;
  private Integer partition;
  private Long offset;
  private Event event;

  public EventResponse() { }

  public EventResponse(String status, Integer partition, Long offset, Event event) {
    this.status = status;
    this.partition = partition;
    this.offset = offset;
    this.event = event;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Integer getPartition() {
    return partition;
  }

  public void setPartition(Integer partition) {
    this.partition = partition;
  }

  public Long getOffset() {
    return offset;
  }

  public void setOffset(Long offset) {
    this.offset = offset;
  }

  public Event getEvent() {
    return event;
  }

  public void setEvent(Event event) {
    this.event = event;
  }

  public static class Event {

    private String id;
    private String type;
    private String timestamp;
    private Object payload;

    public Event() { }

    public Event(String id, String type, String timestamp, Object payload) {
      this.id = id;
      this.type = type;
      this.timestamp = timestamp;
      this.payload = payload;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public String getTimestamp() {
      return timestamp;
    }

    public void setTimestamp(String timestamp) {
      this.timestamp = timestamp;
    }

    public Object getPayload() {
      return payload;
    }

    public void setPayload(Object payload) {
      this.payload = payload;
    }
  }
}
