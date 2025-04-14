package project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.DTO.UserDTO;
import project.DTO.UserDetailsDTO;
import project.model.*;

import project.repository.AddressAndTimeRepository;
import project.repository.UserRepository;

import java.time.Duration;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final AddressAndTimeRepository addressAndTimeRepository;
    private final UserCoordinatesService userCoordinatesService;
    private final GeocodingService geocodingService;
    private final TwoGisRouteService twoGisRouteService;

    @Autowired
    public UserService(UserRepository userRepository, AddressAndTimeRepository addressAndTimeRepository,
                       UserCoordinatesService userCoordinatesService, GeocodingService geocodingService,
                       TwoGisRouteService twoGisRouteService) {
        this.userRepository = userRepository;
        this.addressAndTimeRepository = addressAndTimeRepository;
        this.userCoordinatesService = userCoordinatesService;
        this.geocodingService = geocodingService;
        this.twoGisRouteService = twoGisRouteService;
    }

    public void saveUserData(UserDetailsDTO userDetailsDTO) {
        User user = new User();

        user.setTelegramUserId(userDetailsDTO.getTelegramUserId());
        user.setUsername(userDetailsDTO.getUsername());
        user.setFirstName(userDetailsDTO.getFirstName());
        userRepository.save(user);
    }

    public void saveUserWork(UserDTO userDTO) {
        Optional<User> userOpt = userRepository.findByTelegramUserId(userDTO.getTelegramUserId());

        AddressAndTime addressAndTime = new AddressAndTime();
        addressAndTime.setTelegramUserId(userDTO.getTelegramUserId());
        addressAndTime.setHomeAddress(userDTO.getHomeAddress());
        addressAndTime.setWorkAddress(userDTO.getWorkAddress());
        addressAndTime.setWorkStartTime(userDTO.getWorkStartTime());
        addressAndTimeRepository.save(addressAndTime);

        Location homeLocation = geocodingService.getCoordinates(userDTO.getHomeAddress());
        Location workLocation = geocodingService.getCoordinates(userDTO.getWorkAddress());

        Coordinates homeCoordinates = new Coordinates(homeLocation.getCoordinatesLat(), homeLocation.getCoordinatesLon());
        Coordinates workCoordinates = new Coordinates(workLocation.getCoordinatesLat(), workLocation.getCoordinatesLon());
        Long travelTime = twoGisRouteService.getRouteDuration(homeCoordinates, workCoordinates);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println("User:" + user.getTelegramUserId());
            user.setAddressAndTime(addressAndTime);

            if (user.getTimeZone() == null){
                user.setTimeZone(homeLocation.getTimeZone());
            }
            UserCoordinates newCoordinates = userCoordinatesService
                    .saveCoordinates(userDTO.getTelegramUserId(), homeCoordinates, workCoordinates);
            user.setUserCoordinates(newCoordinates);

            user.setTravelTime(travelTime);
            userRepository.save(user);

        }
    }

    public User getUserByTelegramUserId(Long telegramUserId) {
        Optional<User> userOpt = userRepository.findByTelegramUserId(telegramUserId);
        return userOpt.get();
    }
}
