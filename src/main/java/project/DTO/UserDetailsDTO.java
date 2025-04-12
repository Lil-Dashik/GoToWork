package project.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class UserDetailsDTO {
    private Long telegramUserId;
    private String username;
    private String firstName;
    private String lastName;
    private String timeZone;
    public UserDetailsDTO() {}
    public UserDetailsDTO(Long telegramUserId, String username, String firstName, String lastName, String timeZone) {
        this.telegramUserId = telegramUserId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.timeZone = timeZone;
    }
}
