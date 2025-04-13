package project.Dadata;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GeocodingSuggestion {
    private GeocodingData data;
    public GeocodingSuggestion() {}
    public GeocodingSuggestion(GeocodingData data) {
        this.data = data;
    }
}
