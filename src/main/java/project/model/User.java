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
//    private String homeAddress;
//    private String workAddress;
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date workStartTime;
    @Column(name = "telegram_user_id", unique = true)
    private Long telegramUserId;
    @Column(name = "notification_enabled", nullable = false)
    private boolean notificationEnabled = true;
    private String username;
    private String firstName;
//    private String timeZone;
//    private String languageCode;

//    @NotEmpty(message = "Name should not be empty")
//    @Size(min = 2, max = 30, message = "Name should be between 2 and 30 characters")
//    private String name;

//    @NotNull(message = "Age should not be empty")
//    @Min(value = 3, message = "Age should be greater than 3")
//    private Integer age;

//    @NotEmpty(message = "Email should not be empty")
//    @Email(message = "Email should be valid")
//    private String email;
}