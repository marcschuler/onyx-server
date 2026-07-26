package de.marcschuler.onyxserver.config.starter;

import de.marcschuler.onyxserver.repository.ServerRepository;
import de.marcschuler.onyxserver.service.ServerService;
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
        serverRepository.findAll().stream().forEach(server -> {
            log.info("Starting server {} ({})",server.getName(),server.getId());
        });
    }
}
