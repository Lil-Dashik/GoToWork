package project.service;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Map;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
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
                .map(user -> {
                    NotificationDTO dto = buildNotificationInfo(user.getTelegramUserId());
                    ZonedDateTime nowInUserZone = ZonedDateTime.now(ZoneId.of(user.getTimeZone()));
                    return Map.entry(dto, nowInUserZone);
                })
                .filter(entry -> {
                    ZonedDateTime notifyTime = entry.getKey().getNotifyTime();
                    ZonedDateTime now = entry.getValue();
                    long diff = Duration.between(notifyTime, now).toMinutes();
                    logger.debug("Проверка уведомления для userId={} | notifyTime={} | now={} | разница={} мин",
                            entry.getKey().getTelegramUserId(), notifyTime, now, diff);
                    return diff >= 0 && diff <= 1;
                })
                .map(Map.Entry::getKey)
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

        if (user.getTimeZone() == null) {
            throw new IllegalStateException("Не указан часовой пояс для пользователя " + telegramId);
        }

        ZoneId userZone = ZoneId.of(user.getTimeZone());
        LocalTime workStart = user.getAddressAndTime().getWorkStartTime();
        Long minTravelTime = user.getTravelTime();

        Duration totalOffset = Duration.ofMinutes(minTravelTime + 30);
        LocalDate today = LocalDate.now(userZone);
        ZonedDateTime workStartZdt = ZonedDateTime.of(today, workStart, userZone);
        ZonedDateTime notifyZdt = workStartZdt.minus(totalOffset);

        String message = String.format("Через 30 минут нужно выходить, чтобы успеть к %s", workStart);

        return new NotificationDTO(
                telegramId,
                message,
                notifyZdt
        );
    }

    public void updateTravelTimeIfNeeded() throws JSONException {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            if (!user.isNotificationEnabled()) continue;

            AddressAndTime data = user.getAddressAndTime();
            if (data == null || user.getUserCoordinates() == null) continue;

            ZoneId zoneId = ZoneId.of(user.getTimeZone());
            ZonedDateTime now = ZonedDateTime.now(zoneId);

            LocalTime workStart = data.getWorkStartTime();
            Duration savedTravel = Duration.ofMinutes(user.getTravelTime());

            ZonedDateTime expectedCheckTime = ZonedDateTime.of(
                    now.toLocalDate(),
                    workStart.minus(savedTravel).minusMinutes(30),
                    zoneId
            );

            logger.debug("Пользователь {}: плановая проверка в {} (разница {} мин)",
                    user.getTelegramUserId(),
                    expectedCheckTime,
                    Duration.between(now, expectedCheckTime).toMinutes());

            if (Duration.between(now, expectedCheckTime).abs().toMinutes() <= 1) {
                Coordinates from = user.getUserCoordinates().getHomeCoordinates();
                Coordinates to = user.getUserCoordinates().getWorkCoordinates();

                long updatedMinutes = twoGisRouteService.getRouteDuration(from, to);
                logger.info("Обновляем travelTime для пользователя {}: старое значение {} мин, новое значение {} мин",
                        user.getTelegramUserId(), user.getTravelTime(), updatedMinutes);

                user.setTravelTime(updatedMinutes);
                userRepository.save(user);
            }
        }
    }
}
