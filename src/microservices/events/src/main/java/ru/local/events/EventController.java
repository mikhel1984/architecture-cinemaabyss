package ru.local.events;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.local.events.configuration.KafkaTopicConfig;
import ru.local.events.dto.EventResponse;
import ru.local.events.dto.MovieEvent;
import ru.local.events.dto.PaymentEvent;
import ru.local.events.dto.UserEvent;

@RestController
@RequestMapping("/api/events")
public class EventController {

  private KafkaTemplate<String, Object> kafkaTemplate;

  @PostMapping("/movie")
  public ResponseEntity<EventResponse> movie(@RequestBody MovieEvent dto)
      throws ExecutionException, InterruptedException {
    return new ResponseEntity<>(
        sendMessage(KafkaTopicConfig.MOVIE_TOPIC, "movie", dto),
        HttpStatus.CREATED
    );
  }

  @PostMapping("/user")
  public ResponseEntity<EventResponse> user(@RequestBody UserEvent dto)
      throws ExecutionException, InterruptedException {
    return new ResponseEntity<>(
        sendMessage(KafkaTopicConfig.USER_TOPIC, "user", dto),
        HttpStatus.CREATED
    );
  }

  @PostMapping("/payment")
  public ResponseEntity<EventResponse> payment(@RequestBody PaymentEvent dto)
      throws ExecutionException, InterruptedException {
    return new ResponseEntity<>(
        sendMessage(KafkaTopicConfig.PAYMENT_TOPIC, "payment", dto),
        HttpStatus.CREATED
    );
  }

  @GetMapping("/health")
  public Map<String, Object> health() {
    return Map.of("status", true);
  }

  private EventResponse sendMessage(String topicName, String eventType, Object msg)
      throws ExecutionException, InterruptedException {
    final var sendResult = kafkaTemplate.send(topicName, UUID.randomUUID().toString(), msg).get();
    final var event = new EventResponse.Event(
        sendResult.getProducerRecord().key(),
        eventType,
        OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
        msg
    );
    return new EventResponse(
        "success",
        sendResult.getRecordMetadata().partition(),
        sendResult.getRecordMetadata().offset(),
        event
    );
  }

  @Autowired
  public void setKafkaTemplate(KafkaTemplate<String, Object> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }
}
