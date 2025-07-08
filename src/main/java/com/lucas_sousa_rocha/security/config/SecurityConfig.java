package com.lucas_sousa_rocha.security.config;


import com.lucas_sousa_rocha.security.service.UserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfig is a Spring configuration class responsible for defining the security
 * configuration and authentication mechanisms for the application.
 *
 * This class integrates with Spring Security to define security constraints and manage user authentication
 * and authorization within the application. It provides the following key features:
 *
 * - Configures a {@link SecurityFilterChain} that specifies the security rules for incoming HTTP requests,
 *   including endpoints that are public and those that require authentication.
 * - Sets up a {@link DaoAuthenticationProvider} to handle authentication using a custom {@link UserDetailsService}
 *   and a {@link PasswordEncoder}.
 * - Provides an {@link AuthenticationManager} bean that is required to handle authentication requests.
 *
 * The configuration disables certain security measures for specific scenarios, such as CSRF protection and
 * X-Frame-Options headers, especially for development purposes or use with H2 console.
 *
 * This class also defines custom login and logout behavior, such as specifying custom login pages
 * and post-logout redirection URLs.
 */
@Configuration
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;


    public SecurityConfig(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register", "/h2-console/**", "/forgot-password", "/reset-password").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/home", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .authenticationProvider(daoAuthenticationProvider());

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

}