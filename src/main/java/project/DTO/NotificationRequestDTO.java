package project.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalTime;
@Getter
@Setter
@ToString
public class NotificationRequestDTO {
    private Long telegramUserId;
    private String timeZone;
    private LocalTime workStartTime;
    private Long travelTime;

    public NotificationRequestDTO() {}

    public NotificationRequestDTO(Long telegramUserId, String timeZone, LocalTime workStartTime, Long travelTime) {
        this.telegramUserId = telegramUserId;
        this.timeZone = timeZone;
        this.workStartTime = workStartTime;
        this.travelTime = travelTime;
    }
}
