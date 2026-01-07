package de.marcschuler.webrtcserver.config;

import de.marcschuler.webrtcserver.dto.SignedContent;
import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
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

    @PostConstruct
    public void ignoreSignedContentGenericType() {
        // Tell SpringDoc: "Whenever you see SignedContent<*>, treat it as just SignedContent"
        SpringDocUtils.getConfig().replaceWithClass(SignedContent.class, SignedContent.class);
    }

    @Bean
    public OpenApiCustomizer schemaCustomizer() {
        var polymorphySchemas = new HashMap<Class<?>, ResolvedSchema>();

        return openApi -> {
            Schema<?> anySchema = new Schema<>()
                    .type("object")
                    .additionalProperties(true)
                    .description("Valid JSON data without a given schema (internally: JsonNode)");


            openApi.addSecurityItem(new SecurityRequirement()
                            .addList("jwt-auth"))
                    .components(new Components()
                            .addSecuritySchemes("jwt-auth", new SecurityScheme()
                                    .name("jwt-auth")
                                    .type(SecurityScheme.Type.HTTP)
                                    .scheme("bearer")
                                    .bearerFormat("JWT")));
            openApi.schema("JsonNode", anySchema);
            var messageTypes = new ArrayList<String>();

            new Reflections("de.marcschuler.webrtcserver.webclient.messages")
                    .getSubTypesOf(MessageBody.class)
                    .stream()
                    .filter(c -> !Modifier.isAbstract(c.getModifiers())) // ignore abstract classes
                    .map(c -> {
                        ResolvedSchema resolvedSchema = ModelConverters.getInstance()
                                .resolveAsResolvedSchema(new AnnotatedType(c));

                        var baseSchemas = new ArrayList<Schema<?>>();
                        Class<?> cla = c.getSuperclass();
                        if (cla != null && cla != Object.class) { // Object.class leads to null schema
                            var baseSchema = polymorphySchemas.computeIfAbsent(cla, aClass -> {
                                var resolvedBaseSchema = ModelConverters.getInstance()
                                        .resolveAsResolvedSchema(new AnnotatedType(aClass));
                                openApi.schema(resolvedBaseSchema.schema.getName(), resolvedBaseSchema.schema);
                                return resolvedBaseSchema;
                            });
                            baseSchemas.add(new Schema<>().$ref("#/components/schemas/" + baseSchema.schema.getName()));
                            cla = cla.getSuperclass();
                        }
                        if (!baseSchemas.isEmpty())
                            resolvedSchema.schema.allOf(baseSchemas);

                        var typeSchema = new io.swagger.v3.oas.models.media.StringSchema()
                                ._enum(List.of(c.getSimpleName()))
                                .readOnly(true);
                        resolvedSchema.schema.addProperty("type", typeSchema);
                        resolvedSchema.schema.addRequiredItem("type");
                        messageTypes.add(c.getSimpleName());
                        return resolvedSchema;
                    })
                    .forEach(schema -> {
                        log.info("Adding schema{}", schema.schema.getName());
                        openApi.schema(schema.schema.getName(), schema.schema);
                    });

            Schema<String> enumSchema = new Schema<>();
            enumSchema.setType("string");
            enumSchema.setDescription("A list of all messages");
            enumSchema.setEnum(messageTypes);
            openApi.schema("MessageTypes", enumSchema);

        }

                ;
    }

}
