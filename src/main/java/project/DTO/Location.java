package project.DTO;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class Location {
    private double coordinatesLat;
    private double coordinatesLon;
    private String timeZone;

    public Location(double coordinatesLat, double coordinatesLon, String timeZone) {
        this.coordinatesLat = coordinatesLat;
        this.coordinatesLon = coordinatesLon;
        this.timeZone = timeZone;
    }

    public Location() {
    }

}
