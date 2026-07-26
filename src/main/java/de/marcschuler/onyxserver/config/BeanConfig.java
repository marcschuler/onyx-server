package de.marcschuler.onyxserver.config;

import de.marcschuler.onyxserver.data.file.PreviewFormat;
import de.marcschuler.onyxserver.dto.SignedContent;
import de.marcschuler.onyxserver.webclient.messages.MessageBody;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.jsontype.NamedType;
import tools.jackson.databind.module.SimpleModule;

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


    @Bean
    public OpenApiCustomizer schemaCustomizer() {
        return openApi -> {
            var polymorphySchemas = new HashMap<Class<?>, ResolvedSchema>();
            var messageTypes = new ArrayList<String>();

            Schema<?> anySchema = new Schema<>()
                    .type("object")
                    .additionalProperties(true)
                    .description("Valid JSON data without a given schema (internally: JsonNode)");
            openApi.getComponents().addSchemas("JsonNode", anySchema);

            openApi.addSecurityItem(new SecurityRequirement().addList("jwt-auth"));
            openApi.getComponents().addSecuritySchemes("jwt-auth",
                    new SecurityScheme()
                            .name("jwt-auth")
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
            );

            new Reflections("de.marcschuler.onyxserver.webclient.messages")
                    .getSubTypesOf(MessageBody.class)
                    .stream()
                    .filter(c -> !Modifier.isAbstract(c.getModifiers()))
                    .forEach(c -> {
                        ResolvedSchema resolvedSchema = ModelConverters.getInstance()
                                .resolveAsResolvedSchema(new AnnotatedType(c));

                        Class<?> superclass = c.getSuperclass();
                        List<Schema<?>> allOfSchemas = new ArrayList<>();

                        while (superclass != null && superclass != Object.class && superclass != Record.class) {
                            var baseSchema = polymorphySchemas.computeIfAbsent(superclass, cls -> {
                                var resolvedBase = ModelConverters.getInstance()
                                        .resolveAsResolvedSchema(new AnnotatedType(cls));
                                // Only add if not already present
                                if (!openApi.getComponents().getSchemas().containsKey(resolvedBase.schema.getName())) {
                                    openApi.getComponents().addSchemas(resolvedBase.schema.getName(), resolvedBase.schema);
                                }
                                return resolvedBase;
                            });

                            allOfSchemas.add(new Schema<>().$ref("#/components/schemas/" + baseSchema.schema.getName()));
                            superclass = superclass.getSuperclass();
                        }

                        if (!allOfSchemas.isEmpty()) {
                            resolvedSchema.schema.allOf(allOfSchemas);
                        }

                        var typeSchema = new io.swagger.v3.oas.models.media.StringSchema()
                                ._enum(List.of(c.getSimpleName()))
                                .readOnly(true);
                        resolvedSchema.schema.addProperty("type", typeSchema);
                        resolvedSchema.schema.addRequiredItem("type");

                        messageTypes.add(c.getSimpleName());

                        if (!openApi.getComponents().getSchemas().containsKey(resolvedSchema.schema.getName())) {
                            openApi.getComponents().addSchemas(resolvedSchema.schema.getName(), resolvedSchema.schema);
                        }
                    });

            Schema<String> enumSchema = new Schema<>();
            enumSchema.setType("string");
            enumSchema.setDescription("A list of all message types");
            enumSchema.setEnum(messageTypes);
            openApi.getComponents().addSchemas("MessageTypes", enumSchema);

            createSchemaFromEnum(openApi, PreviewFormat.class);
        };
    }

    private void createSchemaFromEnum(OpenAPI openApi, Class<?> c) {
        if (!c.isEnum())
            throw new IllegalStateException();

        var enumSchema = new Schema<String>();
        enumSchema.setType("string");
        enumSchema.setEnum(Arrays.stream(c.getEnumConstants())
                .map(Object::toString)
                .toList());
        openApi.getComponents().addSchemas(c.getSimpleName(), enumSchema);
    }

}
