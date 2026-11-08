package de.marcschuler.onyxserver.config.starter;

import de.marcschuler.onyxserver.repository.ServerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Logs the active servers on startup. The default server itself is created by Liquibase
 * (changeSet 002-default-server, see DefaultServerSecretsChange)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FirstTimeStartupRunner implements CommandLineRunner {

    private final ServerRepository serverRepository;

    @Override
    public void run(String... args) {
        serverRepository.findAll().forEach(server ->
                log.info("Starting server {} ({})", server.getName(), server.getId()));
    }
}
