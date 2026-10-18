package de.marcschuler.onyxserver.config;

import de.marcschuler.onyxserver.data.ClientState;
import de.marcschuler.onyxserver.data.User;
import de.marcschuler.onyxserver.service.AuthService;
import de.marcschuler.onyxserver.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.text.ParseException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    @InjectMocks
    private SecurityConfig.JwtAuthenticationFilter filter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    @AfterEach
    void resetContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void missingAuthorizationHeader_continuesWithoutAuthentication() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void nonBearerAuthorizationHeader_continuesWithoutAuthentication() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNz");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void validBearerToken_setsAuthentication() throws ServletException, IOException, com.nimbusds.jose.JOSEException, ParseException {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        when(authService.verifyJWT("valid-token")).thenReturn("user-id");
        var user = new User();
        user.setId("user-id");
        user.setState(ClientState.ACTIVE);
        when(userService.findById("user-id")).thenReturn(Optional.of(user));

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertTrue(auth.getPrincipal() instanceof SecurityConfig.AuthenticatedUser);
        assertEquals(user, ((SecurityConfig.AuthenticatedUser) auth.getPrincipal()).user());
    }

    @Test
    void bannedUser_throwsForbidden() throws com.nimbusds.jose.JOSEException, ParseException {
        when(request.getHeader("Authorization")).thenReturn("Bearer banned-token");
        when(authService.verifyJWT("banned-token")).thenReturn("banned-id");
        var user = new User();
        user.setId("banned-id");
        user.setState(ClientState.BANNED);
        when(userService.findById("banned-id")).thenReturn(Optional.of(user));

        assertThrows(ResponseStatusException.class, () ->
                filter.doFilterInternal(request, response, filterChain));
    }
}
