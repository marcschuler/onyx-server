package de.marcschuler.onyxserver.config.liquibase;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Gives Liquibase custom changes (e.g. {@link DefaultServerSecretsChange}) access to Spring beans.
 * Liquibase instantiates custom changes itself, so they can not be injected.
 * <p>
 * Only use stateless beans (e.g. CryptoService). The EntityManagerFactory depends on Liquibase,
 * so JPA repositories / services are not available while migrations run.
 */
@Component(LiquibaseSpringContext.BEAN_NAME)
public class LiquibaseSpringContext implements ApplicationContextAware {

    public static final String BEAN_NAME = "liquibaseSpringContext";
    /**
     * Name of the SpringLiquibase bean of the spring boot autoconfiguration
     */
    private static final String LIQUIBASE_BEAN_NAME = "liquibase";

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static <T> T getBean(Class<T> type) {
        if (context == null)
            throw new IllegalStateException("Spring context is not available yet");
        return context.getBean(type);
    }

    /**
     * Makes sure the context is set before Liquibase runs its migrations
     */
    @Configuration
    static class LiquibaseDependsOnContext {
        @Bean
        static BeanFactoryPostProcessor liquibaseDependsOnSpringContext() {
            return (ConfigurableListableBeanFactory beanFactory) -> {
                if (!beanFactory.containsBeanDefinition(LIQUIBASE_BEAN_NAME))
                    return;
                var definition = beanFactory.getBeanDefinition(LIQUIBASE_BEAN_NAME);
                var dependsOn = new ArrayList<String>();
                if (definition.getDependsOn() != null)
                    dependsOn.addAll(Arrays.asList(definition.getDependsOn()));
                dependsOn.add(BEAN_NAME);
                definition.setDependsOn(dependsOn.toArray(String[]::new));
            };
        }
    }
}
