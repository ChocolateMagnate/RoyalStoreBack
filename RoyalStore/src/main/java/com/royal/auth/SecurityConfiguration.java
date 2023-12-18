package com.royal.auth;

import com.royal.auth.filters.EmailAndPasswordLoginFilter;
import com.royal.auth.filters.OAuth2Filter;
import com.royal.repositories.UserRepository;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Autowired
    private UserRepository userRepository;

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
        return email -> userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("User by email " + email + " is not found."));
    }
    @Bean
    public AuthenticationProvider authenticationProvider() {
        var provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetails());
        return provider;
    }
    @Bean
    public AuthenticationManager authenticationManager(@NotNull AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return JwtDecoders.fromIssuerLocation("https://accounts.google.com");
    }

    @Bean
    public JwtService jwt() {
        return new JwtService();
    }

    @Bean
    public EmailAndPasswordLoginFilter emailAndPasswordLoginFilter() {
        return new EmailAndPasswordLoginFilter();
    }

    @Bean
    public OAuth2Filter oAuth2Filter() {
        return new OAuth2Filter();
    }

}
