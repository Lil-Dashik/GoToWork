package project.DTO;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import project.service.NotificationService;

@Component
@RequiredArgsConstructor
public class TravelTimeScheduler {
    private final NotificationService notificationService;

    @Scheduled(cron = "0 */5 * * * *")
    public void updateTravelTimes() {
        notificationService.updateTravelTimeIfNeeded();
    }
}
