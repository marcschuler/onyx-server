package de.marcschuler.onyxserver.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PermissionAnnotationSmokeTest {

    @Test
    void permissionAnnotation_existsAndTargetsMethods() {
        var annotation = Permission.class;
        assertNotNull(annotation);
        assertEquals(java.lang.annotation.ElementType.METHOD, annotation.getAnnotation(java.lang.annotation.Target.class).value()[0]);
    }

    @Test
    void permissionAnnotation_usesPreAuthorize() {
        var preAuthorize = Permission.class.getAnnotation(PreAuthorize.class);
        assertNotNull(preAuthorize);
        assertEquals("@permissionService.checkAccessForSinglePermission(authentication.name, #permission)", preAuthorize.value());
    }

    @Test
    void permissionAnnotation_hasValueAttribute() throws NoSuchMethodException {
        Method valueMethod = Permission.class.getMethod("value");
        assertEquals(String.class, valueMethod.getReturnType());
    }
}
