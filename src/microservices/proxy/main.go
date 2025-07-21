package main

import (
	"log"
	"net/http"
	"os"
	"io"
	"strconv"
	"strings"
	"math/rand"
)

var customTransport = http.DefaultTransport

func main() {
	http.HandleFunc("/", proxyHandler)

	port := os.Getenv("PORT")
	if port == "" {
		port = "8000"
	}
	log.Printf("Starting proxy on port %s", port)
	log.Fatal(http.ListenAndServe(":"+port, nil))
}


func proxyHandler (w http.ResponseWriter, r *http.Request) {
	// choose goal service
	var newUrl string
	if strings.HasPrefix(r.URL.String(), "/api/events") {
		newUrl = os.Getenv("EVENTS_SERVICE_URL")
	} else {
		if os.Getenv("GRADUAL_MIGRATION") == "true" {
			// try microservice
			n, err := strconv.Atoi(os.Getenv("MOVIES_MIGRATION_PERCENT"))
			if err != nil {
				log.Println(err)
				return
			}

			if rand.Intn(100) < n {
				newUrl = os.Getenv("MOVIES_SERVICE_URL")
			} else {
				newUrl = os.Getenv("MONOLITH_URL")
			}			
		} else {
			newUrl = os.Getenv("MOVIES_SERVICE_URL")
		}
	}

	newUrl += r.URL.String()

	// retranslate

	proxyReq, err := http.NewRequest(r.Method, newUrl, r.Body)
	if err != nil {
		log.Println(err)
		return
	}

	for k, v := range r.Header {
		for _, val := range v {
			proxyReq.Header.Add(k, val)
		}
    }

	resp, err := customTransport.RoundTrip(proxyReq)
	if err != nil {
		log.Println(err)
		return
	}
	defer resp.Body.Close()

	for name, values := range resp.Header {
		for _, value := range values {
			w.Header().Add(name, value)
		}
	}

	w.WriteHeader(resp.StatusCode)

	io.Copy(w, resp.Body)
}
