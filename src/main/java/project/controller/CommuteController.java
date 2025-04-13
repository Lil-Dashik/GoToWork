package project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.DTO.UserDTO;
import project.DTO.UserDetailsDTO;
import project.service.CommuteService;
import project.service.UserService;

@RestController
@RequestMapping("/api/commute")
public class CommuteController {
    private final UserService userService;

    @Autowired
    public CommuteController(UserService userService) {
        this.userService = userService;
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

}
