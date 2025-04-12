package project.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
@Getter
@Setter
@ToString
public class CommuteTime {
    private Date date;
    private Integer durationMinutes;
    private Date departureTime;
    private Date workStartTime;
    private Location location;
    public CommuteTime() {}
    public CommuteTime(Date date, Integer durationMinutes, Date departureTime, Date workStartTime) {
        this.date = date;
        this.durationMinutes = durationMinutes;
        this.departureTime = departureTime;
        this.workStartTime = workStartTime;
    }
}
