
package com.personnel.insight;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService users(
            PasswordEncoder encoder,
            @Value("${APP_ADMIN_USER:admin}") String username,
            @Value("${APP_ADMIN_PASSWORD:}") String password) {

        if (password.isBlank() || password.length() < 12) {
            throw new IllegalStateException(
                "APP_ADMIN_PASSWORD must have at least 12 characters"
            );
        }

        return new InMemoryUserDetailsManager(
            User.withUsername(username)
                .password(encoder.encode(password))
                .roles("ADMIN")
                .build()
        );
    }

    @Bean
    public SecurityFilterChain chain(HttpSecurity http)
            throws Exception {

        http.authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/login.html",
                "/login.css",
                "/login.js",
                "/api/csrf",
                "/favicon.ico",
                "/error"
            ).permitAll()
            .anyRequest().hasRole("ADMIN")
        );

        http.formLogin(form -> form
            .loginPage("/login.html")
            .loginProcessingUrl("/login")
            .defaultSuccessUrl("/", true)
            .failureUrl("/login.html?error")
            .permitAll()
        );

        http.logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login.html?logout")
            .invalidateHttpSession(true)
            .clearAuthentication(true)
            .deleteCookies("JSESSIONID", "XSRF-TOKEN")
        );

        // CSRF được lưu theo phiên trên máy chủ.
        // Không dùng CookieCsrfTokenRepository cũ.
        http.csrf(csrf -> csrf
            .csrfTokenRepository(
                new HttpSessionCsrfTokenRepository()
            )
        );

        http.headers(headers -> headers
            .frameOptions(frame -> frame.deny())
            .contentSecurityPolicy(csp -> csp.policyDirectives(
                "default-src 'self'; " +
                "script-src 'self'; " +
                "style-src 'self' 'unsafe-inline'; " +
                "img-src 'self' data:; " +
                "object-src 'none'; " +
                "frame-ancestors 'none'; " +
                "base-uri 'self'; " +
                "form-action 'self'"
            ))
        );

        return http.build();
    }
}
