package ru.local.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class PaymentEvent {

  @JsonProperty(value = "payment_id", required = true)
  private Integer paymentId;
  @JsonProperty(value = "user_id", required = true)
  private Integer userId;
  @JsonProperty(required = true)
  private BigDecimal amount;
  @JsonProperty(required = true)
  private String status;
  @JsonProperty(required = true)
  private String timestamp;
  @JsonProperty(value = "method_type")
  private String methodType;

  public Integer getPaymentId() {
    return paymentId;
  }

  public void setPaymentId(Integer paymentId) {
    this.paymentId = paymentId;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  public String getMethodType() {
    return methodType;
  }

  public void setMethodType(String methodType) {
    this.methodType = methodType;
  }

  @Override
  public String toString() {
    return "PaymentEvent{" +
        "paymentId=" + paymentId +
        ", userId=" + userId +
        ", amount=" + amount +
        ", status='" + status + '\'' +
        ", timestamp='" + timestamp + '\'' +
        ", methodType='" + methodType + '\'' +
        '}';
  }
}
