package ru.movie.events;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentEvent {
    private Integer payment_id;
    private Integer user_id;
    private Double amount;
    private String status; // completed, failed, refunded, etc.
    private String timestamp;
    private String methodType;
}
