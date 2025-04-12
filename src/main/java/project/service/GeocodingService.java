package project.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import project.model.Coordinates;
import project.model.GeocodingResponse;
import project.model.GeocodingSuggestion;

import org.springframework.http.HttpHeaders;

@Service
public class GeocodingService {
    private static final String API_KEY = "079ab8ffc4cc858af3494978f47b670e5f5aa516";
    private static final String GEOCODE_URL = "https://suggestions.dadata.ru/suggestions/api/4_1/rs/geolocate/address";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public GeocodingService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }
    public Coordinates getCoordinates(String address) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Token " + API_KEY);
        headers.add("Content-Type", "application/json");

        // Формируем запрос
        String requestBody = "{\"query\": \"" + address + "\"}";

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        // Отправляем запрос
        ResponseEntity<String> response = restTemplate.postForEntity(GEOCODE_URL, entity, String.class);

        // Обработка ответа
        try {
            GeocodingResponse geocodingResponse = objectMapper.readValue(response.getBody(), GeocodingResponse.class);
            if (!geocodingResponse.getSuggestions().isEmpty()) {
                GeocodingSuggestion suggestion = geocodingResponse.getSuggestions().get(0);
                double lat = suggestion.getData().getGeoLat();
                double lon = suggestion.getData().getGeoLon();
                return new Coordinates(lat, lon);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
