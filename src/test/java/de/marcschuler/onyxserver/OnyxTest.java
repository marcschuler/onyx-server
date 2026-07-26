package de.marcschuler.onyxserver;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts={
        "classpath:sql/cleanup.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts={
        "classpath:sql/base.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts={
        "classpath:sql/cleanup.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public @interface OnyxTest {
}
