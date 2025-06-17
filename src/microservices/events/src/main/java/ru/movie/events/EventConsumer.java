package ru.movie.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "${kafka.topics.movie}", groupId = "event-service-group")
    public void consumeMovieEvents(String message) {
        try {
            Event event = objectMapper.readValue(message, Event.class);
            logger.info("Processing movie event: {}", event);
            // Implement business logic for movie events
        } catch (JsonProcessingException e) {
            logger.error("Error processing movie event: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "${kafka.topics.user}", groupId = "event-service-group")
    public void consumeUserEvents(String message) {
        try {
            Event event = objectMapper.readValue(message, Event.class);
            logger.info("Processing user event: {}", event);
            // Implement business logic for user events
        } catch (JsonProcessingException e) {
            logger.error("Error processing user event: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "${kafka.topics.payment}", groupId = "event-service-group")
    public void consumePaymentEvents(String message) {
        try {
            Event event = objectMapper.readValue(message, Event.class);
            logger.info("Processing payment event: {}", event);
            // Implement business logic for payment events
        } catch (JsonProcessingException e) {
            logger.error("Error processing payment event: {}", e.getMessage());
        }
    }
}
