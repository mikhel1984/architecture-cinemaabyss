package main

import (
	"log"
	"net/http"
	"os"
	"io"
	"strconv"
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
	monolithAddr := os.Getenv("MONOLITH_URL")
	newUrl := monolithAddr + r.URL.String()
	
	if os.Getenv("GRADUAL_MIGRATION") == "true" {
		// try microservice
		n, err := strconv.Atoi(os.Getenv("MOVIES_MIGRATION_PERCENT"))
		if err != nil {
			log.Println(err)
			return
		}

		if rand.Intn(100) < n {
			serviceAddr := os.Getenv("MOVIES_SERVICE_URL")
			newUrl = serviceAddr + r.URL.String()
		}
	}

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
