package ru.local.events.configuration;

import static ru.local.events.configuration.KafkaConsumerConfig.CONSUMER_GROUP_ID;
import static ru.local.events.configuration.KafkaTopicConfig.MOVIE_TOPIC;
import static ru.local.events.configuration.KafkaTopicConfig.PAYMENT_TOPIC;
import static ru.local.events.configuration.KafkaTopicConfig.USER_TOPIC;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

@Configuration
public class KafkaEventListener {

  private static final Logger log = LoggerFactory.getLogger(KafkaEventListener.class);

  @KafkaListener(topics = MOVIE_TOPIC, groupId = CONSUMER_GROUP_ID)
  public void movieListener(Object message) {
    log.info("Received event: {}", message);
  }

  @KafkaListener(topics = USER_TOPIC, groupId = CONSUMER_GROUP_ID)
  public void userListener(Object message) {
    log.info("Received event: {}", message);
  }

  @KafkaListener(topics = PAYMENT_TOPIC, groupId = CONSUMER_GROUP_ID)
  public void paymentListener(Object message) {
    log.info("Received event: {}", message);
  }
}
