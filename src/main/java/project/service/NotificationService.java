package project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.DTO.Coordinates;
import project.DTO.NotificationDTO;
import project.model.AddressAndTime;
import project.model.User;
import project.repository.UserRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class NotificationService {
    private final UserRepository userRepository;
    private final TwoGisRouteService twoGisRouteService;

    @Autowired
    public NotificationService(UserRepository userRepository, TwoGisRouteService twoGisRouteService) {
        this.userRepository = userRepository;
        this.twoGisRouteService = twoGisRouteService;
    }

    public NotificationDTO buildNotificationInfo(Long telegramId) {
        User user = userRepository.findByTelegramUserId(telegramId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalTime workStart = user.getAddressAndTime().getWorkStartTime();
        Long minTravelTime = user.getTravelTime();
        Duration travelTime = Duration.ofMinutes(minTravelTime);
        LocalTime leaveHomeTime = workStart.minus(travelTime);

        LocalTime notifyTime = leaveHomeTime.minusMinutes(30);

        LocalDateTime now = LocalDateTime.now(ZoneId.of(user.getTimeZone()));
        LocalDateTime todayNotifyTime = LocalDateTime.of(now.toLocalDate(), notifyTime);

        String message = String.format("Через 30 минут нужно выходить, чтобы успеть к %s", workStart);

        return new NotificationDTO(telegramId, message, todayNotifyTime);
    }

    public void updateTravelTimeIfNeeded() {
        List<User> users = userRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (User user : users) {
            if (!user.isNotificationEnabled()) continue;

            AddressAndTime data = user.getAddressAndTime();
            if (data == null || user.getUserCoordinates() == null) continue;

            LocalTime workStart = data.getWorkStartTime();
            Duration savedTravel = Duration.ofMinutes(user.getTravelTime());

            LocalTime expectedCheckTime = workStart.minus(savedTravel).minusMinutes(30);
            LocalDateTime todayCheck = LocalDateTime.of(now.toLocalDate(), expectedCheckTime);

            if (Duration.between(now, todayCheck).abs().toMinutes() <= 1) {
                Coordinates from = user.getUserCoordinates().getHomeCoordinates();
                Coordinates to = user.getUserCoordinates().getWorkCoordinates();

                long updatedMinutes = twoGisRouteService.getRouteDuration(from, to);
                user.setTravelTime(updatedMinutes);
                userRepository.save(user);
            }
        }
    }
}
