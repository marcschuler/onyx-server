package de.marcschuler.onyxserver.config;


import de.marcschuler.onyxserver.data.file.PreviewFormat;
import de.marcschuler.onyxserver.webclient.messages.MessageBody;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

@Configuration
@Slf4j
public class OpenApiConfig {

    @Bean
    public OperationCustomizer controllerPrefixedOperationId() {
        return (operation, handler) -> {
            //avoids ever changing operationIds like all_3, all_4, ...
            //it generates like groupController_all and on code generation the prefix is removed
            var annotation = handler.getMethodAnnotation(Operation.class);
            if (annotation != null && !StringUtils.isEmpty(annotation.operationId()))
                return operation;
            var classname = handler.getBeanType().getSimpleName();
            var methodname = handler.getMethod().getName();
            operation.setOperationId(classname + "_" + methodname);
            return operation;
        };
    }

    @Bean
    public OpenApiCustomizer schemaCustomizer() {
        return openApi -> {

            //add JsonNode as "any" type
            Schema<?> anySchema = new Schema<>()
                    .type("object")
                    .additionalProperties(true)
                    .description("Valid JSON data without a given schema (internally: JsonNode)");
            openApi.getComponents().addSchemas("JsonNode", anySchema);

            //add bearer
            openApi.addSecurityItem(new SecurityRequirement().addList("jwt-auth"));
            openApi.getComponents().addSecuritySchemes("jwt-auth",
                    new SecurityScheme()
                            .name("jwt-auth")
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
            );

            // add all webclient messages and create a name enum
            var messageClasses = new Reflections("de.marcschuler.onyxserver.webclient.messages")
                    .getSubTypesOf(MessageBody.class);
            messageClasses.stream()
                    .filter(c -> !Modifier.isAbstract(c.getModifiers()))
                    .forEach(c -> {
                        var schemas = ModelConverters.getInstance().readAll(c);
                        schemas.forEach(openApi.getComponents()::addSchemas);
                        String name = c.getSimpleName(); // add "type" field as static string
                        schemas.get(name)
                                .addProperty("type", new StringSchema()._enum(List.of(name)).readOnly(true))
                                .addRequiredItem("type");

                    });

            // add type enum from simple nae
            createMessageTypeEnum(openApi,
                    messageClasses.stream()
                            .map(Class::getSimpleName)
                            .sorted(String::compareTo).toList());

            // add all DTO classes, including inheritance
            new Reflections("de.marcschuler.onyxserver.dto")
                    .getSubTypesOf(Object.class).forEach(c -> {
                        var schemas = ModelConverters.getInstance().readAll(c);
                        schemas.forEach(openApi.getComponents()::addSchemas);
                    });

            createSchemaFromEnum(openApi, PreviewFormat.class);


        };
    }

    private void createMessageTypeEnum(OpenAPI openApi, List<String> messageTypes) {
        Schema<String> enumSchema = new Schema<>();
        enumSchema.setType("string");
        enumSchema.setDescription("A list of all message types");
        enumSchema.setEnum(messageTypes);
        openApi.getComponents().addSchemas("MessageTypes", enumSchema);
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
