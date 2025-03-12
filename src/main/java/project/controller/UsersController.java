package project.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import project.model.User;
import project.repository.UserRepository;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UsersController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String getTableUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/addUser")
    public String addNewUser(Model model) {
        model.addAttribute("user", new User());
        System.out.println("addNewUser");
        return "addNewUser";
    }

    @PostMapping("/addUser")
    public String addNewUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "addNewUser";
        }
        userRepository.save(user);
        return "redirect:/users";
    }

    @GetMapping("/editUser")
    public String editUser(@RequestParam("id") Long id, Model model) {
        User user = userRepository.findById(id).get();
        model.addAttribute("user", user);
        System.out.println("editUser");
        return "editUser";
    }

    @PatchMapping("/editUser")
    public String editUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            return "editUser";
        }
        User editUser = userRepository.findById(user.getId()).get();
        editUser.setName(user.getName());
        editUser.setAge(user.getAge());
        editUser.setEmail(user.getEmail());
        userRepository.save(editUser);
        System.out.println("editUserrr");
        return "redirect:/users";
    }

    @GetMapping("/deleteUser")
    public String deleteUser(@RequestParam("id") Long id, Model model) {
        User user = userRepository.findById(id).get();
        model.addAttribute("user", user);
        return "deleteUser";
    }

    @DeleteMapping("/deleteUser")
    public String deleteUser(@RequestParam("id") Long id) {
        userRepository.deleteById(id);
        return "redirect:/users";
    }
}
