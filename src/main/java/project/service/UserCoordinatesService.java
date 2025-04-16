package project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.DTO.Coordinates;
import project.model.UserCoordinates;
import project.repository.UserCoordinatesRepository;

import java.util.Optional;

@Service
public class UserCoordinatesService {
    private final UserCoordinatesRepository userCoordinatesRepository;


    @Autowired
    public UserCoordinatesService(UserCoordinatesRepository userCoordinatesRepository) {
        this.userCoordinatesRepository = userCoordinatesRepository;
    }

    public UserCoordinates saveCoordinates(Long telegramUserId, Coordinates homeCoordinates, Coordinates workCoordinates) {
        Optional<UserCoordinates> existing = userCoordinatesRepository.findByTelegramUserId(telegramUserId);

        UserCoordinates newCoordinates = existing.orElse(new UserCoordinates());
        newCoordinates.setTelegramUserId(telegramUserId);
        newCoordinates.setHomeLatitude(homeCoordinates.getLatitude());
        newCoordinates.setHomeLongitude(homeCoordinates.getLongitude());
        newCoordinates.setWorkLatitude(workCoordinates.getLatitude());
        newCoordinates.setWorkLongitude(workCoordinates.getLongitude());
        return userCoordinatesRepository.save(newCoordinates);
    }
}
