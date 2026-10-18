package de.marcschuler.onyxserver.config;

import de.marcschuler.onyxserver.dto.SignedContent;
import de.marcschuler.onyxserver.service.StorageService;
import de.marcschuler.onyxserver.webclient.messages.MessageBody;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.jsontype.NamedType;
import tools.jackson.databind.module.SimpleModule;

import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
@Slf4j
public class BeanConfig {

    @Bean
    public ScheduledExecutorService scheduledExecutorService() {
        return Executors.newScheduledThreadPool(8);
    }

    @PostConstruct
    public void ignoreSignedContentGenericType() {
        // Tell SpringDoc: "Whenever you see SignedContent<*>, treat it as just SignedContent"
        SpringDocUtils.getConfig().replaceWithClass(SignedContent.class, SignedContent.class);
    }


    @Bean
    public StorageService storageService(
            @Value("${onyx.storage.files.files}") Path basePath,
            @Value("${onyx.storage.files.cache}") Path cacheBasePath){
        return new StorageService(basePath,cacheBasePath);
    }

    @Bean
    public JsonMapperBuilderCustomizer registerAnimalSubtypes() {
        return builder -> {
            var module = new SimpleModule("webclient-messages");

            var names = new ArrayList<String>();
            new Reflections("de.marcschuler.onyxserver.webclient.messages")
                    .getSubTypesOf(MessageBody.class)
                    .stream()
                    .filter(c -> !Modifier.isAbstract(c.getModifiers()))
                    .filter(c -> !Modifier.isInterface(c.getModifiers()))
                    .forEach(c -> {
                        names.add(c.getSimpleName());
                        module.registerSubtypes(new NamedType(c, c.getSimpleName()));
                    });
            log.info("Initialised events for jackson {}", names);

            builder.addModule(module);
        };
    }


}
