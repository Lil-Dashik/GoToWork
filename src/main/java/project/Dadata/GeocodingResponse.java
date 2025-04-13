package project.Dadata;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GeocodingResponse {
    private double geo_lat;
    private double geo_lon;
    @JsonProperty("timezone")
    private String timeZone;
    public GeocodingResponse(){}
    public GeocodingResponse(double lat, double lon, String timeZone) {
        this.geo_lat = lat;
        this.geo_lon = lon;
        this.timeZone = timeZone;
    }
}
