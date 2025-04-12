package project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.DTO.UserDTO;
import project.DTO.UserDetailsDTO;
import project.model.AddressAndTime;
import project.model.User;
import project.repository.AddressAndTimeRepository;
import project.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final AddressAndTimeRepository addressAndTimeRepository;
    @Autowired
    public UserService(UserRepository userRepository, AddressAndTimeRepository addressAndTimeRepository) {
        this.userRepository = userRepository;
        this.addressAndTimeRepository = addressAndTimeRepository;
    }
    public void saveUserData(UserDetailsDTO userDetailsDTO) {
        User user = new User();

        user.setTelegramUserId(userDetailsDTO.getTelegramUserId());
        user.setUsername(userDetailsDTO.getUsername());
        user.setFirstName(userDetailsDTO.getFirstName());
//        user.setTimeZone(userDetailsDTO.getTimeZone());

        userRepository.save(user);
    }
    public void saveUserWork(UserDTO userDTO) {
        Optional<User> userOpt = userRepository.findByTelegramUserId(userDTO.getTelegramUserId());
        AddressAndTime addressAndTime = new AddressAndTime();
        addressAndTime.setHomeAddress(userDTO.getHomeAddress());
        addressAndTime.setWorkAddress(userDTO.getWorkAddress());
        addressAndTime.setWorkStartTime(userDTO.getWorkStartTime());
        addressAndTimeRepository.save(addressAndTime);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println("User:" + user.getTelegramUserId());
            user.setAddressAndTime(addressAndTime);
            userRepository.save(user);  // Сохраняем изменения
        }
    }
    public User getUserByTelegramUserId(Long telegramUserId) {
        Optional<User> userOpt = userRepository.findByTelegramUserId(telegramUserId);
        return userOpt.get();
    }
}
