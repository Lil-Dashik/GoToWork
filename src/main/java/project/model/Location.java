package project.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class Location {
    private String home;
    private String work;
    public Location() {}
    public Location(String home, String work) {
        this.home = home;
        this.work = work;
    }

}
