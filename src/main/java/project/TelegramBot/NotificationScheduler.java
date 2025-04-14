package project.TelegramBot;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import project.DTO.NotificationDTO;
import project.model.User;
import project.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class NotificationScheduler {
    private final RestTemplate restTemplate = new RestTemplate();
    private final CommuteBot commuteBot;
    private final UserRepository userRepository;
    @Autowired
    public NotificationScheduler(CommuteBot commuteBot, UserRepository userRepository) {
        this.commuteBot = commuteBot;
        this.userRepository = userRepository;
    }

    @Scheduled(fixedRate = 300000)
    public void checkAndSendNotifications() {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            if (!user.isNotificationEnabled()) continue;

            try {
                String url = "http://localhost:8080/api/commute/" + user.getTelegramUserId();
                NotificationDTO dto = restTemplate.getForObject(url, NotificationDTO.class);

                if (dto != null && dto.getNotifyTime().isBefore(LocalDateTime.now())) {
                    commuteBot.sendMessage(dto.getTelegramUserId(), dto.getMessage());
                }
            } catch (Exception e) {
                System.err.println("Ошибка при получении уведомления для пользователя " + user.getTelegramUserId());
                e.printStackTrace();
            }
        }
    }
}
