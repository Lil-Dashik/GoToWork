package project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CommuteService {
    private final GeocodingService geocodingService;
    @Autowired
    public CommuteService(GeocodingService geocodingService) {
        this.geocodingService = geocodingService;
    }
//    public CommuteTime calculateCommuteTime(String homeAddress, String workAddress, Date workStartTime) {
//        UserCoordinates homeCoords = geocodingService.getCoordinates(homeAddress);
//        UserCoordinates workCoords = geocodingService.getCoordinates(workAddress);

        // Расчет времени в пути через Yandex API
//        int durationMinutes = getDurationBetweenCoordinates(homeCoords, workCoords);

        // Вычисляем время выезда
//        Date departureTime = calculateDepartureTime(workStartTime, durationMinutes);
//
//    / /   // Возвращаем объект CommuteTime с результатами
//        CommuteTime commuteTime = new CommuteTime();
//        commuteTime.setWorkStartTime(workStartTime);
////        commuteTime.setDurationMinutes(durationMinutes);
////        commuteTime.setDepartureTime(departureTime);
//
//        return commuteTime;
//    }


//    private int getDurationBetweenCoordinates(Coordinates homeCoords, Coordinates workCoords) {
//        // В этой функции ты будешь обращаться к Yandex API для расчета времени в пути
//        return 30; // Пример: время в пути 30 минут
//    }

    private Date calculateDepartureTime(Date workStartTime, int durationMinutes) {
        // Логика для расчета времени выезда
        long departureTimeMillis = workStartTime.getTime() - durationMinutes * 60 * 1000;
        return new Date(departureTimeMillis);
    }
}
