package main

import (
	"log"
	"os"
	"net/http"
	"encoding/json"
	//"time"
    //"github.com/confluentinc/confluent-kafka-go/kafka"
)

type Event struct {
	ID		string		`json:"id"`
	Type	string		`json:"type"`
	Timestamp	string	`json:"timestamp"`
	Payload	[]string	`json:"payload"`
}

type Movie struct {
	Status 	string	`json:"status"`
	Partition	int	`json:"partition"`
	Offset	int		`json:"offset"`
	Event	Event	`json:"event"`
}

func main() {

	http.HandleFunc("/api/events/health", handleHealth)
	http.HandleFunc("/api/events/movie", handleMovies)
	http.HandleFunc("/api/events/user", handleUser)
	http.HandleFunc("/api/events/payment", handlePayment)

	// producer, err := kafka.NewProducer(&kafka.ConfigMap{
	// 	"bootstrap.servers": "host1:9092,host2:9092",
	// })	
	// if err != nil {
	// 	log.Println(err)
	// 	return
	// }
	// defer producer.Close()
	
	// consumer, err := kafka.NewConsumer(&kafka.ConfigMap{
	// 	"bootstrap.servers":    "host1:9092,host2:9092",
	// })
	// if err != nil {
	// 	log.Println(err)
	// 	return
	// }
	// defer consumer.Close()

	// Start server
	port := os.Getenv("PORT")
	if port == "" {
		port = "8082" 
	}
	log.Printf("Starting events microservice on port %s", port)
	log.Fatal(http.ListenAndServe(":"+port, nil))
}

func handleHealth(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(map[string]bool{"status": true})
}

func handleMovies(w http.ResponseWriter, r *http.Request) {
	switch r.Method {
	case "POST":
		createMovie(w, r)
	default:
		http.Error(w, "Method not allowed", http.StatusMethodNotAllowed)
	}
}

func handleUser(w http.ResponseWriter, r *http.Request) {
	switch r.Method {
	case "POST":
		createMovie(w, r)
	default:
		http.Error(w, "Method not allowed", http.StatusMethodNotAllowed)
	}
}

func handlePayment(w http.ResponseWriter, r *http.Request) {
	switch r.Method {
	case "POST":
		createMovie(w, r)
	default:
		http.Error(w, "Method not allowed", http.StatusMethodNotAllowed)
	}
}


func createMovie(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusCreated)
	var m Movie
	m.Status = "success"
	m.Partition = 0
	m.Offset = 42
	m.Event.ID = "movie-1-viewd"
	m.Event.Type = "movie"
	m.Event.Timestamp = "2023-01-15T14:30:00Z"
	json.NewEncoder(w).Encode(m)
}