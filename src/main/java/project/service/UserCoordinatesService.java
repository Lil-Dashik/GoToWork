package project.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.DTO.Coordinates;
import project.model.UserCoordinates;
import project.repository.UserCoordinatesRepository;

@Service
public class UserCoordinatesService {
    private static final Logger logger = LoggerFactory.getLogger(UserCoordinatesService.class);
    private final UserCoordinatesRepository userCoordinatesRepository;


    @Autowired
    public UserCoordinatesService(UserCoordinatesRepository userCoordinatesRepository) {
        this.userCoordinatesRepository = userCoordinatesRepository;
    }

    public UserCoordinates saveCoordinates(Long telegramUserId, Coordinates homeCoordinates, Coordinates workCoordinates) {
        if (homeCoordinates != null && workCoordinates != null) {
            UserCoordinates newCoordinates = new UserCoordinates(
                    telegramUserId,
                    homeCoordinates.getLatitude(),
                    homeCoordinates.getLongitude(),
                    workCoordinates.getLatitude(),
                    workCoordinates.getLongitude()
            );
            return userCoordinatesRepository.save(newCoordinates);
        } else {
            logger.error("Некоторые координаты не были получены (дом или работа).");
        }
        return null;
    }
}
