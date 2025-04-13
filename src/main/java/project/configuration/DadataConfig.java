package project.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class DadataConfig {
    @Value("${dadata.api.key}")
    private String dadataKey;
    public DadataConfig() {}
    public DadataConfig(String dadataKey) {
        this.dadataKey = dadataKey;
    }

}
