package project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.DTO.UserDTO;
import project.DTO.UserDetailsDTO;
import project.model.User;
import project.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public void saveUserData(UserDTO userDTO, UserDetailsDTO userDetailsDTO) {
        User user = new User();
        user.setHomeAddress(userDTO.getHomeAddress());
        user.setWorkAddress(userDTO.getWorkAddress());
        user.setWorkStartTime(userDTO.getWorkStartTime());

        user.setTelegramUserId(userDetailsDTO.getTelegramUserId());
        user.setUsername(userDetailsDTO.getUsername());
        user.setFirstName(userDetailsDTO.getFirstName());
        user.setLastName(userDetailsDTO.getLastName());
        user.setTimeZone(userDetailsDTO.getTimeZone());

        userRepository.save(user);
    }
    public User getUserByTelegramUserId(Long telegramUserId) {
        return userRepository.findByTelegramUserId(telegramUserId);
    }


}
