package com.royal.auth;

import com.royal.auth.filters.EmailAndPasswordLoginFilter;
import com.royal.auth.filters.OAuth2Filter;
import com.royal.users.service.UserService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtDecoder jwtDecoder;
    private final JwtService jwtService;

    @Autowired
    SecurityConfiguration(UserService userService, PasswordEncoder passwordEncoder,
                          JwtDecoder jwtDecoder, JwtService jwtService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtDecoder = jwtDecoder;
        this.jwtService = jwtService;
    }

    private static final String[] authenticatedEndpoints = {"/get-cart", "/get-liked", "/get-purchased",
            "/add-product-to-cart", "/add-product-to-liked", "/purchase", "/remove-product-from-cart",
            "/remove-product-from-liked", "/remove-product-from-purchased"};
    private static final String[] adminEndpoints = {"/create-smartphone", "/update-smartphone/",
            "/delete-smartphone/", "/create-laptop", "/update-laptop/", "/delete-laptop/"};

    @Bean
    public SecurityFilterChain filterChain(@NotNull HttpSecurity http) throws Exception {
        http
            .addFilterBefore(emailAndPasswordLoginFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(oAuth2Filter(), UsernamePasswordAuthenticationFilter.class)
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(authenticatedEndpoints).authenticated()
                    .requestMatchers(adminEndpoints).hasAuthority("admin")
                    .anyRequest().permitAll());
        return http.build();
    }

    @Bean
    public UserDetailsService userDetails() {
        return userService::loadUserByEmail;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        var provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetails());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(@NotNull AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }




    @Bean
    public EmailAndPasswordLoginFilter emailAndPasswordLoginFilter() {
        return new EmailAndPasswordLoginFilter(jwtService, userService);
    }

    @Bean
    public OAuth2Filter oAuth2Filter() {
        return new OAuth2Filter(jwtService);
    }

}
