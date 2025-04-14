package project.service;


import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import project.configuration.TwoGisConfig;
import project.DTO.Coordinates;

import org.springframework.http.HttpHeaders;

@Service
public class TwoGisRouteService {
    private static final Logger logger = LoggerFactory.getLogger(TwoGisRouteService.class);
    private final RestTemplate restTemplate;
    private final TwoGisConfig twoGisConfig;

    private static final String ROUTING_URL = "https://routing.api.2gis.com/routing/7.0.0/global";
    @Autowired
    public TwoGisRouteService(RestTemplate restTemplate, TwoGisConfig twoGisConfig) {
        this.restTemplate = restTemplate;
        this.twoGisConfig = twoGisConfig;
    }
    public Long getRouteDuration(Coordinates from, Coordinates to) {
        String url = ROUTING_URL + "?key=" + twoGisConfig.getGisKey();
        JSONObject requestBody = new JSONObject();
        JSONArray points = new JSONArray();
        points.put(new JSONObject()
                .put("type", "stop")
                .put("lon", from.getLongitude())
                .put("lat", from.getLatitude()));
        points.put(new JSONObject()
                .put("type", "stop")
                .put("lon", to.getLongitude())
                .put("lat", to.getLatitude()));
        requestBody.put("points", points);
        requestBody.put("traffic_mode", "jam");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);
        logger.info("Request body: {}", requestBody);
        logger.info("Request headers: {}", headers);
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                JSONObject json = new JSONObject(response.getBody());
                JSONArray resultArray = json.getJSONArray("result");

                if (resultArray.length() > 0) {
                    logger.info("Response status: {}", response.getStatusCode());
                    logger.info("Response body: {}", response.getBody());
                    JSONObject route = resultArray.getJSONObject(0);
                    int seconds = route.getInt("total_duration");
                    long travelTime = Math.round(seconds / 60.0);
                    logger.info("minutes: {}", travelTime);
                    return travelTime;
                } else {
                    throw new RuntimeException("Маршрут не найден в ответе 2ГИС");
                }
            } catch (Exception e) {
                throw new RuntimeException("Ошибка при обработке ответа от 2ГИС API", e);
            }
        } else {
            throw new RuntimeException("Ошибка при запросе к 2ГИС API: " + response.getStatusCode());
        }
    }

}
