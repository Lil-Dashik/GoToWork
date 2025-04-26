package project.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import project.dto.NotificationDTO;

import java.util.List;

@Component
public class NotificationSender {
    private static final Logger logger = LoggerFactory.getLogger(NotificationSender.class);
    private final NotificationService notificationService;
    private final AlertProducer alertProducer;

    @Autowired
    public NotificationSender(NotificationService notificationService, AlertProducer alertProducer) {
        this.notificationService = notificationService;
        this.alertProducer = alertProducer;
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void sendNotifications() {
        try {
            List<NotificationDTO> notifications = notificationService.getNotificationsToSend();
            for (NotificationDTO notification : notifications) {
                alertProducer.sendAlert(notification);
                logger.info("Отправлено уведомление для userId={}", notification.getTelegramUserId());
            }
        } catch (Exception e) {
            logger.error("Ошибка при отправке уведомлений через Kafka", e);
        }
    }
}
