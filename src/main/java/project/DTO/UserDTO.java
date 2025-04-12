package project.DTO;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalTime;
import java.util.Date;
@Getter
@Setter
@ToString
public class UserDTO {
    private Long telegramUserId;
    private String homeAddress;
    private String workAddress;
    private LocalTime workStartTime;
    public UserDTO(){}
    public UserDTO(String homeAddress, String workAddress, LocalTime workStartTime, Long telegramUserId) {
        this.homeAddress = homeAddress;
        this.workAddress = workAddress;
        this.workStartTime = workStartTime;
        this.telegramUserId = telegramUserId;
    }

}
