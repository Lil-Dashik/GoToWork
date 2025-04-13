package project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "telegram_user_id", unique = true)
    private Long telegramUserId;
    @Column(name = "notification_enabled", nullable = false)
    private boolean notificationEnabled = true;
    private String username;
    private String firstName;
    @OneToOne
    @JoinColumn(name = "address_and_time_id")
    private AddressAndTime addressAndTime;
    @OneToOne
    @JoinColumn(name="coordinates_id")
    private UserCoordinates userCoordinates;
//    private String timeZone;
//    private String languageCode;
}