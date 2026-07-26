package de.marcschuler.onyxserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("webrtc")
@Data
public class WebRTConfig {

    private Config config;

    @Data
    public static class Config{
        private List<IceConfig> ice;
    }

    @Data
    public static class IceConfig{
        private String urls;
        private String username;
        private String credential;
    }
}
