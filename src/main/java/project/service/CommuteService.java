package project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.model.AddressAndTime;
import project.model.Coordinates;
import project.model.UserCoordinates;
import project.repository.AddressAndTimeRepository;
import project.repository.UserCoordinatesRepository;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Date;

@Service
public class CommuteService {
    private final UserCoordinatesRepository userCoordinatesRepository;
    private final AddressAndTimeRepository addressAndTimeRepository;
    private final TwoGisRouteService yandexRouteService;

    @Autowired
    public CommuteService(UserCoordinatesRepository userCoordinatesRepository,
                          AddressAndTimeRepository addressAndTimeRepository,
                          TwoGisRouteService yandexRouteService) {
        this.userCoordinatesRepository = userCoordinatesRepository;
        this.addressAndTimeRepository = addressAndTimeRepository;
        this.yandexRouteService = yandexRouteService;
    }

//    public Duration calculateCommuteTime(Long telegramUserId){
//        UserCoordinates coordinates = userCoordinatesRepository.findByTelegramUserId(telegramUserId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//        AddressAndTime addressAndTime = addressAndTimeRepository.findByTelegramUserId(telegramUserId)
//                .orElseThrow(() -> new RuntimeException("Time not found"));
//        Coordinates home = new Coordinates(coordinates.getHomeLatitude(), coordinates.getHomeLongitude());
//        Coordinates work = new Coordinates(coordinates.getWorkLatitude(), coordinates.getWorkLongitude());
//        LocalTime workStartTime = addressAndTime.getWorkStartTime();
//        return yandexRouteService.getRouteDuration(home, work, workStartTime);
//    }

    private Date calculateDepartureTime(Date workStartTime, int durationMinutes) {
        // Логика для расчета времени выезда
        long departureTimeMillis = workStartTime.getTime() - durationMinutes * 60 * 1000;
        return new Date(departureTimeMillis);
    }
}
