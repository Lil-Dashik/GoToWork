package project.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import project.DTO.Coordinates;

@Getter
@Setter
@Entity
@Table(name="users_coordinates")
public class UserCoordinates {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "telegram_user_id", unique = true, nullable = false)
    private Long telegramUserId;

    @Column(name = "home_latitude", nullable = false)
    private double homeLatitude;

    @Column(name = "home_longitude", nullable = false)
    private double homeLongitude;

    @Column(name = "work_latitude", nullable = false)
    private double workLatitude;

    @Column(name = "work_longitude", nullable = false)
    private double workLongitude;
    @OneToOne(mappedBy = "userCoordinates")
    private User user;

    public UserCoordinates() {}

    public UserCoordinates(Long telegramUserId, double homeLatitude, double homeLongitude, double workLatitude, double workLongitude) {
        this.telegramUserId = telegramUserId;
        this.homeLatitude = homeLatitude;
        this.homeLongitude = homeLongitude;
        this.workLatitude = workLatitude;
        this.workLongitude = workLongitude;
    }
    public UserCoordinates(double homeLatitude, double homeLongitude, double workLatitude, double workLongitude) {
        this.homeLatitude = homeLatitude;
        this.homeLongitude = homeLongitude;
        this.workLatitude = workLatitude;
        this.workLongitude = workLongitude;
    }
    public Coordinates getHomeCoordinates() {
        return new Coordinates(homeLatitude, homeLongitude);
    }

    public Coordinates getWorkCoordinates() {
        return new Coordinates(workLatitude, workLongitude);
    }

}
