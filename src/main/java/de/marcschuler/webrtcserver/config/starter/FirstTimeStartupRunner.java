package de.marcschuler.webrtcserver.config.starter;

import de.marcschuler.webrtcserver.repository.ServerRepository;
import de.marcschuler.webrtcserver.service.ServerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FirstTimeStartupRunner implements CommandLineRunner {

    private final ServerService serverService;

    private final ServerRepository serverRepository;

    @Value("${onyx.init.startup-wizzard}")
    private boolean startupWizzard;

    @Override
    public void run(String... args) {
        if (serverRepository.count() == 0 && startupWizzard) {
            log.info("This seems to be the first start. Hello :)");
            log.info("Generating a default server");
            var server = serverService.generateDefault();
            log.info("Server with id '{}' and public key '{}' generated", server.getId(), server.getKeys().toPublicJWK().toJSONString());
        }
    }
}
