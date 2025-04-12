package project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.DTO.UserDTO;
import project.DTO.UserDetailsDTO;
import project.DTO.CommuteRequestDTO;
import project.model.User;
import project.service.CommuteService;
import project.service.UserService;

@RestController
@RequestMapping("/api/commute")
public class CommuteController {
    private final UserService userService;
    private final CommuteService commuteService;
    @Autowired
    public CommuteController(UserService userService, CommuteService commuteService) {
        this.userService = userService;
        this.commuteService = commuteService;
    }
    @PostMapping("/start")
    public String startCommand(@RequestBody ) {}

    @PostMapping("/goWork")
    public String handleCommuteRequest(@RequestBody CommuteRequestDTO commuteRequestDTO) {
        if (commuteRequestDTO.getUserDTO() == null || commuteRequestDTO.getUserDetailsDTO() == null) {
            return "Ошибка: данные пользователя не были переданы.";
        }
        UserDTO userDTO = commuteRequestDTO.getUserDTO();
        UserDetailsDTO userDetailsDTO = commuteRequestDTO.getUserDetailsDTO();
        userService.saveUserData(userDTO, userDetailsDTO);
        User user = userService.getUserByTelegramUserId(userDetailsDTO.getTelegramUserId());
        commuteService.calculateCommuteTime(user.getHomeAddress(), user.getWorkAddress(), user.getWorkStartTime());
        return "Данные сохранены и расчет времени выполнен!";
    }
}
