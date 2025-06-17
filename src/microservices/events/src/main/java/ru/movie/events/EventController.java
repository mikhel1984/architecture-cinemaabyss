package ru.movie.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@Slf4j
@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topics.movie}")
    private String movieTopic;

    @Value("${kafka.topics.user}")
    private String userTopic;

    @Value("${kafka.topics.payment}")
    private String paymentTopic;

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> healthCheck() {
        return ResponseEntity.ok(new HealthResponse(true));
    }

    @PostMapping("/movie")
    public ResponseEntity<EventResponse> handleMovieEvent(@RequestBody MovieEvent movieEvent) {
        Event event = createEvent("movie",
                String.format("movie-%d-%s", movieEvent.getMovie_id(), movieEvent.getAction()),
                movieEvent);

        return sendEventToKafka(event, movieTopic);
    }

    @PostMapping("/user")
    public ResponseEntity<EventResponse> handleUserEvent(@RequestBody UserEvent userEvent) {
        Event event = createEvent("user",
                String.format("user-%d-%s", userEvent.getUser_id(), userEvent.getAction()),
                userEvent);

        return sendEventToKafka(event, userTopic);
    }

    @PostMapping("/payment")
    public ResponseEntity<EventResponse> handlePaymentEvent(@RequestBody PaymentEvent paymentEvent) {
        Event event = createEvent("payment",
                String.format("payment-%d-%s", paymentEvent.getPayment_id(), paymentEvent.getStatus()),
                paymentEvent);

        return sendEventToKafka(event, paymentTopic);
    }

    private Event createEvent(String type, String id, Object payload) {
        Event event = new Event();
        event.setId(id);
        event.setType(type);
        event.setTimestamp(LocalDateTime.now().toString());
        event.setPayload(payload);
        return event;
    }

    private ResponseEntity<EventResponse> sendEventToKafka(Event event, String topic) {
        try {
            String eventJson = JsonUtils.toJson(event);
            var future = kafkaTemplate.send(topic, eventJson);
            var result = future.get(); // Wait for the send to complete

            ResponseEntity success = new ResponseEntity(
                    new EventResponse(
                            "success",
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset(),
                            event
                    ),
                    HttpStatusCode.valueOf(201)
            );
            return success;
        } catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(new EventResponse("error", -1, -1L, null));
        }
    }

}