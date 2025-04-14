package project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.DTO.NotificationDTO;
import project.DTO.UserDTO;
import project.DTO.UserDetailsDTO;
import project.service.CommuteService;
import project.service.NotificationService;
import project.service.UserService;

@RestController
@RequestMapping("/api/commute")
public class CommuteController {
    private final UserService userService;
    private final NotificationService notificationService;

    @Autowired
    public CommuteController(UserService userService, NotificationService notificationService) {
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @PostMapping("/start")
    public String startCommand(@RequestBody UserDetailsDTO userDetailsDTO) {
        userService.saveUserData(userDetailsDTO);
        return "Данные сохранены";
    }

    @PostMapping("/goToWork")
    public ResponseEntity<String> goToWork(@RequestBody UserDTO userDTO) {
        System.out.println("Received user data: " + userDTO);
        userService.saveUserWork(userDTO);
        return ResponseEntity.ok("Отправим уведомление за 30 минут до выезда!");
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

}
