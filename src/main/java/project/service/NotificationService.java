package project.service;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.DTO.Coordinates;
import project.DTO.NotificationDTO;
import project.model.AddressAndTime;
import project.model.User;
import project.repository.UserRepository;

import java.time.*;
import java.util.Collections;
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

    public List<NotificationDTO> getNotificationsToSend() {
        LocalDate today = LocalDate.now();

        DayOfWeek day = today.getDayOfWeek();
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            return Collections.emptyList();
        }
        List<User> users = userRepository.findAll();

        return users.stream()
                .filter(User::isNotificationEnabled)
                .filter(user -> user.getLastNotificationSent() == null || !user.getLastNotificationSent().isEqual(today))
                .filter(this::isUserProfileComplete)
                .map(User::getTelegramUserId)
                .map(this::buildNotificationInfo)
                .filter(dto -> dto.getNotifyTime().isBefore(LocalDateTime.now()))
                .toList();
    }

    private boolean isUserProfileComplete(User user) {
        return user.getAddressAndTime() != null &&
                user.getAddressAndTime().getWorkStartTime() != null &&
                user.getTimeZone() != null &&
                user.getTravelTime() != null;
    }


    public NotificationDTO buildNotificationInfo(Long telegramId) {
        User user = userRepository.findByTelegramUserId(telegramId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        ZoneId userZone = ZoneId.of(user.getTimeZone());
        if (user.getTimeZone() == null) {
            throw new IllegalStateException("Не указан часовой пояс для пользователя " + telegramId);
        }
        LocalTime workStart = user.getAddressAndTime().getWorkStartTime();
        Long minTravelTime = user.getTravelTime();
        Duration travelTime = Duration.ofMinutes(minTravelTime);

        LocalDate today = LocalDate.now(userZone);
        ZonedDateTime workStartZdt = ZonedDateTime.of(today, workStart, userZone);

        ZonedDateTime leaveHomeZdt = workStartZdt.minus(travelTime);
        ZonedDateTime notifyZdt = leaveHomeZdt.minusMinutes(30);

        String message = String.format("Через 30 минут нужно выходить, чтобы успеть к %s", workStart);

        return new NotificationDTO(
                telegramId,
                message,
                notifyZdt.toLocalDateTime()
        );
    }

    public void updateTravelTimeIfNeeded() throws JSONException {
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
