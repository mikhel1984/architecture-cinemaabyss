package ru.movie.proxy;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

@RestController
public class ProxyController {

    private final Random random = new Random();
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${monolith.url:http://localhost:8080}")
    private String monolithUrl;

    @Value("${movies.service.url:http://localhost:8081}")
    private String moviesServiceUrl;

    @Value("${gradual.migration:false}")
    private boolean gradualMigration;

    @Value("${movies.migration.percent:50}")
    private int moviesMigrationPercent;

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("{\"status\":\"OK\"}");
    }

    @RequestMapping("/api/movies/**")
    public ResponseEntity<byte[]> proxyMoviesRequest(HttpServletRequest request) throws IOException {
        String targetUrl = shouldRouteToMoviesService() ? moviesServiceUrl : monolithUrl;
        return forwardRequest(request, targetUrl);
    }

    private boolean shouldRouteToMoviesService() {
        return gradualMigration && random.nextInt(100) <= moviesMigrationPercent;
    }

    private ResponseEntity<byte[]> forwardRequest(HttpServletRequest request, String targetBaseUrl) throws IOException {
        String params = request.getParameter("id");
        String path = normalizePath(request.getRequestURI());
        String targetUrl = targetBaseUrl + path;
        if (params != null && !params.isEmpty()) {
            targetUrl += "?id=" + params;
        }

        HttpMethod method = HttpMethod.valueOf(request.getMethod());
        HttpHeaders headers = new HttpHeaders();
        request.getHeaderNames().asIterator()
                .forEachRemaining(headerName -> headers.add(headerName, request.getHeader(headerName)));

        byte[] body = request.getInputStream().readAllBytes();
        HttpEntity<byte[]> httpEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    targetUrl,
                    method,
                    httpEntity,
                    byte[].class);

            HttpHeaders responseHeaders = new HttpHeaders();
            response.getHeaders().forEach((key, values) ->
                    responseHeaders.addAll(key, values));

            return new ResponseEntity<>(
                    response.getBody(),
                    responseHeaders,
                    response.getStatusCode());

        } catch (HttpClientErrorException e) {
            return ResponseEntity
                    .status(e.getStatusCode())
                    .headers(e.getResponseHeaders())
                    .body(e.getResponseBodyAsByteArray());
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(("{\"error\":\"Internal Server Error\"}").getBytes());
        }
    }

    private String normalizePath(String path) {
        if (path.endsWith("/") && path.length() > 1) {
            return path.substring(0, path.length() - 1);
        }
        return path;
    }
}