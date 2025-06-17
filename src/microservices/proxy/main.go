package main

import (
	"fmt"
	"encoding/json"
	"math/rand"
	"net/http"
	"net/http/httputil"
	"net/url"
	"time"
	"log"
	
	"github.com/kelseyhightower/envconfig"
)

type config struct {
	Port string `envconfig:"PORT" default:"8000"`
	MonolithURL string `envconfig:"MONOLITH_URL" default:"http://localhost:8080"`
	MoviesServiceURL string `envconfig:"MOVIES_SERVICE_URL" default:"http://localhost:8081"`
	EventsServiceURL string `envconfig:"EVENTS_SERVICE_URL" default:"http://localhost:8082"`
	GradualMigration bool `envconfig:"GRADUAL_MIGRATION"`
	MoviesMigrationPercent int `envconfig:"MOVIES_MIGRATION_PERCENT"`
}

var cfg config

func main() {
	envconfig.MustProcess("", &cfg)

	http.HandleFunc("/health", handleHealth)
	http.HandleFunc("/api/movies", moviesHandler)
	http.HandleFunc("/api/users", usersHandler)

	// Start server
	log.Printf("Starting proxy microservice on port %s", cfg.Port)
	log.Fatal(http.ListenAndServe(":"+cfg.Port, nil))
}

func handleHealth(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(map[string]bool{"status": true})
}

func moviesHandler(w http.ResponseWriter, r *http.Request) {
	rand.Seed(time.Now().UnixNano())
	if cfg.GradualMigration && rand.Intn(100) <= cfg.MoviesMigrationPercent {
		u, _ := url.Parse(cfg.MoviesServiceURL)
		moviesServiceProxy := httputil.NewSingleHostReverseProxy(u)
		fmt.Println("microservice")
		moviesServiceProxy.ServeHTTP(w, r)
	} else {
		u, _ := url.Parse(cfg.MonolithURL)
		monolithProxy := httputil.NewSingleHostReverseProxy(u)
		fmt.Println("monolith")
		monolithProxy.ServeHTTP(w, r)
	}
}

func usersHandler(w http.ResponseWriter, r *http.Request) {
	rand.Seed(time.Now().UnixNano())
	u, _ := url.Parse(cfg.MonolithURL)
	monolithProxy := httputil.NewSingleHostReverseProxy(u)
	fmt.Println("monolith")
	monolithProxy.ServeHTTP(w, r)
}