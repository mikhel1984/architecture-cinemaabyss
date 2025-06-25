package ru.local.events.configuration;

import java.util.HashMap;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaTopicConfig {

  public static final String MOVIE_TOPIC = "movie-events";
  public static final String USER_TOPIC = "user-events";
  public static final String PAYMENT_TOPIC = "payment-events";

  @Value(value = "${spring.kafka.bootstrap-servers}")
  private String bootstrapAddress;

  @Bean
  public KafkaAdmin kafkaAdmin() {
    final var configs = new HashMap<String, Object>();
    configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    return new KafkaAdmin(configs);
  }

  @Bean(MOVIE_TOPIC)
  public NewTopic movieTopic() {
    return new NewTopic(MOVIE_TOPIC, 1, (short) 1);
  }

  @Bean(USER_TOPIC)
  public NewTopic userTopic() {
    return new NewTopic(USER_TOPIC, 1, (short) 1);
  }

  @Bean(PAYMENT_TOPIC)
  public NewTopic paymentTopic() {
    return new NewTopic(PAYMENT_TOPIC, 1, (short) 1);
  }
}
