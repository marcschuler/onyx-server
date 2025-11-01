package de.marcschuler.webrtcserver.config;

import de.marcschuler.webrtcserver.webclient.events.MessageBody;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.SubTypesScanner;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
@Slf4j
public class BeanConfig {

    @Bean
    public ScheduledExecutorService scheduledExecutorService() {
        return Executors.newScheduledThreadPool(8);
    }

    @Bean
    public OpenApiCustomizer schemaCustomizer() {
        var polymorphySchemas = new HashMap<Class<?>,ResolvedSchema>();

        return openApi -> {
            new Reflections("de.marcschuler.webrtcserver.webclient.events", SubTypesScanner.class)
                    .getSubTypesOf(MessageBody.class)
                    .stream()
                    .map(c -> {
                        ResolvedSchema resolvedSchema = ModelConverters.getInstance()
                                .resolveAsResolvedSchema(new AnnotatedType(c));

                        var baseSchemas = new ArrayList<Schema<?>>();
                      Class<?> cla = c.getSuperclass();
                      while(cla!=null){
                          var baseSchema = polymorphySchemas.computeIfAbsent(cla,aClass -> {
                              var resolvedBaseSchema = ModelConverters.getInstance()
                                      .resolveAsResolvedSchema(new AnnotatedType(c));
                              openApi.schema(resolvedBaseSchema.schema.getName(),resolvedBaseSchema.schema);
                              return resolvedBaseSchema;
                          });
                          baseSchemas.add(baseSchema.schema);
                          cla = cla.getSuperclass();
                      }
                      resolvedSchema.schema.allOf(baseSchemas);

                      /*resolvedSchema.schema.addProperty("type",
                                new StringSchema()
                                        ._default(c.getSimpleName())
                                        .example(c.getSimpleName())
                                        .readOnly(true));*/


                        var typeSchema = new io.swagger.v3.oas.models.media.StringSchema()
                                ._enum(List.of(c.getSimpleName()))
                                .readOnly(true);
                        resolvedSchema.schema.addProperty("type", typeSchema);
                        resolvedSchema.schema.addRequiredItem("type");
                        return resolvedSchema;
                    })
                    .forEach(schema -> {
                        log.info("Adding schema{}", schema.schema.getName());
                        openApi.schema(schema.schema.getName(), schema.schema);
                    });
        };
    }

}
