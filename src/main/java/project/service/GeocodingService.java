package project.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.client.DadataClient;
import project.client.GeocodingResponse;

import project.dto.Location;

import java.util.List;

@Service
public class GeocodingService {
    private static final Logger logger = LoggerFactory.getLogger(GeocodingService.class);
    private final DadataClient dadataClient;


    @Autowired
    public GeocodingService(DadataClient dadataClient) {
        this.dadataClient = dadataClient;
    }

    public Location getCoordinates(String address) {
        logger.info("Отправка запроса в DaData для адреса: {}", address);
        List<GeocodingResponse> responses = dadataClient.cleanAddress(List.of(address));

        if (responses != null && !responses.isEmpty()) {
            GeocodingResponse response = responses.get(0);
            logger.info("Получен ответ DaData: {}", response);
            return new Location(response.getGeo_lat(), response.getGeo_lon(), response.getTimeZone());
        }

        logger.warn("Пустой ответ DaData для адреса: {}", address);
        return null;
    }
}
