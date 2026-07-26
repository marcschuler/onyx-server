package de.marcschuler.onyxserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@SpringBootApplication
@EnableWebSocket
@EnableScheduling
@ConfigurationPropertiesScan
public class OnyxServerApplication {

    static void main(String[] args) {
        SpringApplication.run(OnyxServerApplication.class, args);
    }

}
