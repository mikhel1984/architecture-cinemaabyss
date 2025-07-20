package main

import (
	"log"
	"os"
	"net/http"
	"encoding/json"
	"context"
	//"github.com/twmb/franz-go/pkg/kadm"
	"github.com/twmb/franz-go/pkg/kgo"
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

var client *kgo.Client
var ctx context.Context

func main() {

	http.HandleFunc("/api/events/health", handleHealth)
	http.HandleFunc("/api/events/movie", handleMovies)
	http.HandleFunc("/api/events/user", handleUser)
	http.HandleFunc("/api/events/payment", handlePayment)

	// kafka part
	// https://github.com/twmb/franz-go/tree/master
	seeds := []string{"localhost:9092"}
	var err error
	client, err = kgo.NewClient(
		kgo.SeedBrokers(seeds...),
		kgo.ConsumerGroup("my-group-identifier"),
		kgo.ConsumeTopics("foo"),
	)
	if err != nil {
		log.Println(err)
		return
	}
	defer client.Close()
	ctx = context.Background()

	go receive_msgs()

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

func receive_msgs () {
	// 2.) Consuming messages from a topic
	for {
		fetches := client.PollFetches(ctx)
		if errs := fetches.Errors(); len(errs) > 0 {
			// All errors are retried internally when fetching, but non-retriable errors are
			// returned from polls so that users can notice and take action.
			log.Println(errs)
			return
		}

		// We can iterate through a record iterator...
		iter := fetches.RecordIter()
		for !iter.Done() {
			record := iter.Next()
			//fmt.Println(string(record.Value), "from an iterator!")
			log.Println("receive", string(record.Value))
		}

		// or a callback function.
		// fetches.EachPartition(func(p kgo.FetchTopicPartition) {
		// 	for _, record := range p.Records {
		// 		fmt.Println(string(record.Value), "from range inside a callback!")
		// 	}

		// 	// We can even use a second callback!
		// 	p.EachRecord(func(record *kgo.Record) {
		// 		fmt.Println(string(record.Value), "from a second callback!")
		// 	})
		// })
	}

}

func send_msg(event string) {
	
	// var wg sync.WaitGroup
	// wg.Add(1)
	record := &kgo.Record{Topic: "foo", Value: []byte(event)}
	// client.Produce(ctx, record, func(_ *kgo.Record, err error) {
	// 	defer wg.Done()
	// 	if err != nil {
	// 		log.Println(err)
	// 		return
	// 	}
	// })
	// wg.Wait()

	// Alternatively, ProduceSync exists to synchronously produce a batch of records.
	if err := client.ProduceSync(ctx, record).FirstErr(); err != nil {
		//fmt.Printf("record had a produce error while synchronously producing: %v\n", err)
		log.Println(err)
	}	
}