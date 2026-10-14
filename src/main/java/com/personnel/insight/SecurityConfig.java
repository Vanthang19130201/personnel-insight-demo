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
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {
 @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
 @Bean UserDetailsService users(PasswordEncoder encoder,
    @Value("${APP_ADMIN_USER:admin}") String username,
    @Value("${APP_ADMIN_PASSWORD:}") String password) {
   if(password.isBlank() || password.length()<12) throw new IllegalStateException("Set APP_ADMIN_PASSWORD with at least 12 characters in IntelliJ environment variables before running");
   return new InMemoryUserDetailsManager(User.withUsername(username).password(encoder.encode(password)).roles("ADMIN").build());
 }
 @Bean SecurityFilterChain chain(HttpSecurity http) throws Exception {
   http.authorizeHttpRequests(r->r
    .requestMatchers("/login.html","/login.css","/login.js","/api/csrf","/favicon.ico","/error").permitAll()
    .anyRequest().hasRole("ADMIN"));
   http.formLogin(f->f.loginPage("/login.html").loginProcessingUrl("/login").defaultSuccessUrl("/",true).failureUrl("/login.html?error").permitAll());
   http.logout(l->l.logoutUrl("/logout").logoutSuccessUrl("/login.html?logout"));
   http.csrf(c->c.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
   http.headers(h->h.frameOptions(f->f.deny()).contentSecurityPolicy(c->c.policyDirectives("default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'; img-src 'self' data:; object-src 'none'; frame-ancestors 'none'; base-uri 'self'; form-action 'self'")));
   return http.build();
 }
}
