package main

import (
	"time"
	"net/http"
	"encoding/json"
	"log"

	"github.com/kelseyhightower/envconfig"
	"github.com/IBM/sarama"
)

type config struct {
	Port string `envconfig:"PORT" default:"8082"`
	KafkaBrokers string `envconfig:"KAFKA_BROKERS" default:"http://localhost:9092"`
}

var cfg config

var (
	producer sarama.SyncProducer
	consumer sarama.Consumer
)

func main() {
	envconfig.MustProcess("", &cfg)
	var err error

	// Создание продюсера Kafka
	producer, err = sarama.NewSyncProducer([]string{cfg.KafkaBrokers}, nil)
	if err != nil {
		log.Fatalf("Failed to create producer: %v", err)
	}
	defer producer.Close()

	// Создание консьюмера Kafka
	consumer, err = sarama.NewConsumer([]string{cfg.KafkaBrokers}, nil)
	if err != nil {
		log.Fatalf("Failed to create consumer: %v", err)
	}
	defer consumer.Close()

	go consume("movie-events")
	go consume("user-events")
	go consume("payment-events")

	http.HandleFunc("/api/events/health", handleHealth)

	http.HandleFunc("/api/events/movie", handleMovie)
	http.HandleFunc("/api/events/user", handleUser)
	http.HandleFunc("/api/events/payment", handlePayment)
	
	// Start server
	log.Printf("Starting proxy microservice on port %s", cfg.Port)
	log.Fatal(http.ListenAndServe(":"+cfg.Port, nil))
}

func consume(topic string) {
	partitionConsumer, err := consumer.ConsumePartition(topic, 0, sarama.OffsetNewest)
	if err != nil {
		log.Printf("Failed to create partition consumer for topic %s: %v", topic, err)
		return
	}
	defer partitionConsumer.Close()

	log.Printf("Started consuming messages from topic: %s", topic)

	// Continuously consume messages
	for {
		select {
		case msg := <-partitionConsumer.Messages():
			log.Printf("Received message from topic %s: %s", topic, string(msg.Value))
		case err := <-partitionConsumer.Errors():
			log.Printf("Error consuming from topic %s: %v", topic, err)
		}
	}
}

func produce(topic string) error {
	data := map[string]string{
		"topic": topic,
		"time": time.Now().String(),
	}

	dataJSON, err := json.Marshal(data)
	if err != nil {
		return err
	}

	// Send event to Kafka
	msg := &sarama.ProducerMessage{
		Topic: topic,
		Value: sarama.StringEncoder(dataJSON),
	}

	partition, offset, err := producer.SendMessage(msg)
	if err != nil {
		return err
	}

	log.Printf("Topic: %s sent to partition %d at offset %d", topic, partition, offset)
	
	return nil
}

func handleHealth(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(map[string]bool{"status": true})
}

func handleMovie(w http.ResponseWriter, r *http.Request) {
	err := produce("movie-events")
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusCreated)
	json.NewEncoder(w).Encode(map[string]string{"status": "success"})
}

func handleUser(w http.ResponseWriter, r *http.Request) {
	err := produce("user-events")
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusCreated)
	json.NewEncoder(w).Encode(map[string]string{"status": "success"})
}

func handlePayment(w http.ResponseWriter, r *http.Request) {
	err := produce("payment-events")
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusCreated)
	json.NewEncoder(w).Encode(map[string]string{"status": "success"})
}