package project.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import project.DTO.UserDTO;
import project.DTO.UserDetailsDTO;
import project.model.User;
import project.repository.UserRepository;
import project.service.UserService;

import java.util.List;

//@RestController
//@RequestMapping("/api/users")
//public class UserController {
//    private final UserService userService;
//    @Autowired
//   public UserController(UserService userService) {
//        this.userService = userService;
//    }
//
//    @PostMapping("/saveUser")
//    public String saveUserData(@RequestBody UserDTO userDTO, @RequestBody UserDetailsDTO userDetailsDTO) {
//        userService.saveUserData(userDTO, userDetailsDTO);
//        return "Данные успешно сохранены!";
//    }
//}
