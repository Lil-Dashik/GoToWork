package project.service;

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
import project.DTO.GeocodingResponse;

import org.springframework.http.HttpHeaders;
import project.DTO.Location;

import java.util.List;

@Service
public class GeocodingService {
    private static final Logger logger = LoggerFactory.getLogger(GeocodingService.class);
    private static final String GEOCODE_URL = "https://cleaner.dadata.ru/api/v1/clean/address";
    private final DadataConfig dadataConfig;
    private final RestTemplate restTemplate;


    @Autowired
    public GeocodingService(DadataConfig dadataConfig, RestTemplate restTemplate) {

        this.dadataConfig = dadataConfig;
        this.restTemplate = restTemplate;
    }

    public Location getCoordinates(String address) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Token " + dadataConfig.getDadataKey());
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "application/json");
        headers.add("X-Secret", dadataConfig.getDadataSecret());

        String requestBody = "[\"" + address + "\"]";

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        logger.info("Request body: {}", requestBody);
        logger.info("Request headers: {}", headers);
        ResponseEntity<List<GeocodingResponse>> response = restTemplate.exchange(
                GEOCODE_URL,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );
        logger.info("Response status: {}", response.getStatusCode());
        logger.info("Response body: {}", response.getBody());
        if (response.getBody() != null && !response.getBody().isEmpty()) {
            GeocodingResponse geocodingResponse = response.getBody().get(0);
            return new Location(geocodingResponse.getGeo_lat(), geocodingResponse.getGeo_lon(), geocodingResponse.getTimeZone());

        }
        return null;
    }
}
