package project.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import project.configuration.DadataConfig;
import project.Dadata.GeocodingResponse;
import project.Dadata.GeocodingSuggestion;

import org.springframework.http.HttpHeaders;
import project.model.Coordinates;
import project.model.UserCoordinates;

import java.util.List;

@Service
public class GeocodingService {
    private static final Logger logger = LoggerFactory.getLogger(GeocodingService.class);
    private final DadataConfig dadataConfig;
    private static final String GEOCODE_URL = "https://cleaner.dadata.ru/api/v1/clean/address";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public GeocodingService(RestTemplate restTemplate, ObjectMapper objectMapper, DadataConfig dadataConfig) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.dadataConfig = dadataConfig;
    }
    public Coordinates getCoordinates(String address) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Token " + dadataConfig.getDadataKey());
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "application/json");
        headers.add("X-Secret", "7552d9b3d2427f38c46e09aca23633e161a39a60");

        String requestBody =  "[\"" + address + "\"]";

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();
        logger.info("Request body: {}", requestBody);
        logger.info("Request headers: {}", headers);
        ResponseEntity<List<GeocodingResponse>> response = restTemplate.exchange(
                GEOCODE_URL,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<List<GeocodingResponse>>() {}
        );
        logger.info("Response status: {}", response.getStatusCode());
        logger.info("Response body: {}", response.getBody());
        if (response.getBody() != null && !response.getBody().isEmpty()) {
            GeocodingResponse geocodingResponse = response.getBody().get(0);

            return new Coordinates(geocodingResponse.getGeo_lat(), geocodingResponse.getGeo_lon());
        }
        return null;
    }
}
