package de.marcschuler.webrtcserver.config;

import de.marcschuler.webrtcserver.repository.ServerRepository;
import de.marcschuler.webrtcserver.service.ServerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FirstTimeStartupRunner implements CommandLineRunner {

    private final ServerService serverService;

    private final ServerRepository serverRepository;

    @Override
    public void run(String... args) {
        if (serverRepository.count() == 0) {
            log.info("This seems to be the first start. Hello :)");
            log.info("Generating a default server");
            var server = serverService.generateDefault();
            log.info("Server with id '{}' and public key '{}' generated", server.getId(), server.getKeys().getPublic().toString());
        }
    }
}
