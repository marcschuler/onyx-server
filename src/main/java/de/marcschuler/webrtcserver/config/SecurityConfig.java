package de.marcschuler.webrtcserver.config;

import com.nimbusds.jose.JOSEException;
import de.marcschuler.webrtcserver.data.ClientState;
import de.marcschuler.webrtcserver.data.User;
import de.marcschuler.webrtcserver.service.AuthService;
import de.marcschuler.webrtcserver.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
                .authorizeHttpRequests(auth -> auth
                        //.requestMatchers("/openapi/**").permitAll()
                        .anyRequest().permitAll() //TODO
                )
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Component
    @RequiredArgsConstructor
    public static class JwtAuthenticationFilter extends OncePerRequestFilter {

        private final AuthService authService;
        private final UserService userService;

        @Override
        protected void doFilterInternal(
                HttpServletRequest request,
                HttpServletResponse response,
                FilterChain filterChain
        ) throws ServletException, IOException {
            String token = request.getHeader("Authorization");

            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            } else {
                filterChain.doFilter(request, response);
                return;
            }
            String userId;
            try {
                userId = authService.verifyJWT(token);
            } catch (JOSEException | ParseException e) {
                throw new RuntimeException(e);
            }

            var user = userService.findById(userId).orElseThrow();

            if (user.getState() == ClientState.BANNED)
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);

            Authentication auth =
                    new UsernamePasswordAuthenticationToken(
                            new AuthenticatedUser(user),
                            null,
                            List.of()
                    );

            SecurityContextHolder.getContext().setAuthentication(auth);
            filterChain.doFilter(request, response);
        }
    }

    public record AuthenticatedUser(@NonNull User user) {

    }
}
