package project.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TravelTimeUpdater {
    private static final Logger logger = LoggerFactory.getLogger(TravelTimeUpdater.class);
    private final NotificationService notificationService;

    @Autowired
    public TravelTimeUpdater(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "0 0/15 * * * *")
    public void updateTravelTimes() {
        try {
            notificationService.updateTravelTimeIfNeeded();
            logger.info("Travel time updated");
        } catch (Exception e) {
            logger.error("Ошибка при обновлении времени маршрута", e);
        }
    }
}
