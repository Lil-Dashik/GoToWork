package project.service;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.dto.Coordinates;
import project.dto.Location;
import project.dto.UserDTO;
import project.mapper.AddressAndTimeMapper;
import project.mapper.UserMapper;
import project.model.*;

import project.repository.AddressAndTimeRepository;
import project.repository.UserRepository;

import java.time.LocalDate;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final AddressAndTimeRepository addressAndTimeRepository;
    private final UserCoordinatesService userCoordinatesService;
    private final GeocodingService geocodingService;
    private final TwoGisRouteService twoGisRouteService;
    private final AddressAndTimeMapper addressAndTimeMapper;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository, AddressAndTimeRepository addressAndTimeRepository,
                       UserCoordinatesService userCoordinatesService, GeocodingService geocodingService,
                       TwoGisRouteService twoGisRouteService, AddressAndTimeMapper addressAndTimeMapper,
                       UserMapper userMapper) {
        this.userRepository = userRepository;
        this.addressAndTimeRepository = addressAndTimeRepository;
        this.userCoordinatesService = userCoordinatesService;
        this.geocodingService = geocodingService;
        this.twoGisRouteService = twoGisRouteService;
        this.addressAndTimeMapper = addressAndTimeMapper;
        this.userMapper = userMapper;
    }

    public void saveUserWork(UserDTO userDTO) throws JSONException {
        User user = userRepository.findByTelegramUserId(userDTO.getTelegramUserId())
                .orElseThrow(() -> new RuntimeException("User not found with telegramId=" + userDTO.getTelegramUserId()));

        String oldHomeAddress = user.getAddressAndTime() != null
                ? user.getAddressAndTime().getHomeAddress()
                : null;

        AddressAndTime existing = addressAndTimeRepository
                .findByTelegramUserId(userDTO.getTelegramUserId())
                .orElse(null);

        AddressAndTime addressAndTime = addressAndTimeMapper.toAddressAndTime(userDTO);

        if (existing != null) {
            addressAndTime.setId(existing.getId());
        }

        addressAndTimeRepository.save(addressAndTime);

        Location homeLocation = geocodingService.getCoordinates(userDTO.getHomeAddress());
        Location workLocation = geocodingService.getCoordinates(userDTO.getWorkAddress());

        Coordinates homeCoordinates = addressAndTimeMapper.toCoordinates(homeLocation);
        Coordinates workCoordinates = addressAndTimeMapper.toCoordinates(workLocation);

        Long travelTime = twoGisRouteService.getRouteDurationWithoutTraffic(homeCoordinates, workCoordinates);
        updateUserWithTravelData(user, oldHomeAddress, addressAndTime, homeLocation, homeCoordinates, workCoordinates, travelTime);
    }

    private void updateUserWithTravelData(User user,
                                          String oldHomeAddress,
                                          AddressAndTime addressAndTime,
                                          Location homeLocation,
                                          Coordinates homeCoordinates,
                                          Coordinates workCoordinates,
                                          Long travelTime) {
        user.setAddressAndTime(addressAndTime);

        String newTimeZone = homeLocation.getTimeZone();
        if (timeZoneNeedsRefresh(user.getTimeZone(), oldHomeAddress, addressAndTime.getHomeAddress(), newTimeZone)) {
            user.setTimeZone(newTimeZone);
            logger.info("Обновлён timeZone={} для пользователя {}", newTimeZone, user.getTelegramUserId());
        }

        UserCoordinates newCoordinates = userCoordinatesService
                .saveCoordinates(user.getTelegramUserId(), homeCoordinates, workCoordinates);

        userMapper.updateUser(user, newCoordinates, travelTime);
        userRepository.save(user);
    }


    public void markAsNotifiedToday(Long telegramUserId) {
        userRepository.findByTelegramUserId(telegramUserId).ifPresent(user -> {
            user.setLastNotificationSent(LocalDate.now());
            userRepository.save(user);
        });
    }

    private boolean timeZoneNeedsRefresh(String currentTimeZone, String oldAddress, String newAddress, String newTimeZone) {
        return currentTimeZone == null ||
                oldAddress == null ||
                !oldAddress.equalsIgnoreCase(newAddress) ||
                !currentTimeZone.equalsIgnoreCase(newTimeZone);
    }

    @Transactional
    public void disableNotifications(Long telegramUserId) {
        User user = userRepository.findByTelegramUserId(telegramUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setNotificationEnabled(false);
        userRepository.save(user);
    }
}
