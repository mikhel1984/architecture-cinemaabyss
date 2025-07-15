package main

import (
	"log"
	"net/http"
	"os"
	"io"
	"strings"
	"strconv"
	"math/rand"
)

func main() {
	http.HandleFunc("/", proxyHandler)

	port := "8082"
	log.Printf("Starting proxy on port %s", port)
	log.Fatal(http.ListenAndServe(":"+port, nil))
}

func proxyHandler (w http.ResponseWriter, r *http.Request) {
	// choose goal service
	proxyAddr := os.Getenv("EVENTS_SERVICE_URL")
	monolithAddr := os.Getenv("MONOLITH_URL")
	newUrl := strings.Replace(r.URL.String(), proxyAddr, monolithAddr, -1)

	if os.Getenv("GRADUAL_MIGRATION") == "true" {
		// try microservice
		n, err := strconv.Atoi(os.Getenv("MOVIES_MIGRATION_PERCENT"))
		if err != nil {
			log.Println(err)
			return
		}

		if rand.Intn(100) < n {
			serviceAddr := os.Getenv("MOVIES_SERVICE_URL")
			newUrl = strings.Replace(r.URL.String(), proxyAddr, serviceAddr, -1)
		}
	}

	proxyReq, err := http.NewRequest(r.Method, newUrl, r.Body)
	if err != nil {
		log.Println(err)
		return
	}

	for k, v := range r.Header {
        proxyReq.Header[k] = v
    }

	client := &http.Client{}
    proxyResp, err := client.Do(proxyReq)
	if err != nil {
		log.Println(err)
		return 
	}
	defer proxyResp.Body.Close()

	w.Header().Set("Content-Type", proxyResp.Header.Get("Content-Type"))
    w.WriteHeader(proxyResp.StatusCode)

	// _, err = http.Copy(w, proxyResp.Body)
	// if err != nil {
	// 	log.Println(err)
	// 	return
	// }
	io.Copy(w, proxyResp.Body)
}
