package project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.dto.NotificationDTO;
import project.service.NotificationService;
import project.service.ParseService;
import project.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/commute")
public class CommuteController {
    private final UserService userService;
    private final NotificationService notificationService;
    private final ParseService parseService;

    @Autowired
    public CommuteController(UserService userService, NotificationService notificationService,
                             ParseService parseService) {
        this.userService = userService;
        this.notificationService = notificationService;
        this.parseService = parseService;
    }

    @PostMapping("bot/commands/start")
    public ResponseEntity<String> start(@RequestBody String input) {
        try {
            parseService.parseAndSaveUser(input);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Ошибка при сохранении пользователя");
        }
    }

    @PostMapping("bot/commands/goToWork")
    public ResponseEntity<String> goToWork(@RequestParam Long telegramId, @RequestBody String message) {
        try {
            parseService.parseAndSave(telegramId, message);
            return ResponseEntity.ok("Данные сохранены");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Внутренняя ошибка");
        }
    }

    @GetMapping("/{telegramUserId}")
    public ResponseEntity<NotificationDTO> getNotification(@PathVariable Long telegramUserId) {
        NotificationDTO notification = notificationService.buildNotificationInfo(telegramUserId);

        if (notification != null) {
            return ResponseEntity.ok(notification);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationDTO>> getNotificationsToSend() {
        List<NotificationDTO> notifications = notificationService.getNotificationsToSend();
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/markNotified")
    public ResponseEntity<Void> markUserNotified(@RequestBody Long telegramUserId) {
        userService.markAsNotifiedToday(telegramUserId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("bot/commands/stop")
    public ResponseEntity<String> disableNotifications(@RequestBody Long telegramUserId) {
        userService.disableNotifications(telegramUserId);
        return ResponseEntity.ok("Уведомления отключены");
    }

}
