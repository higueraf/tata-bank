package com.tata.account.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tata.account.dto.ApiResponseDto;
import com.tata.account.service.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final UserDetailsServiceImpl userDetailsService;
  private final ObjectMapper objectMapper;

  public SecurityConfig(UserDetailsServiceImpl userDetailsService, ObjectMapper objectMapper) {
    this.userDetailsService =  userDetailsService;
    this.objectMapper = objectMapper;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(
          HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
    http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                    .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers("/api/users/**").permitAll()
                    .requestMatchers("/api/health").permitAll()
                    .requestMatchers("/doc/**","/swagger-ui/**",
                            "/swagger-resources/*",
                            "/v3/api-docs/**")
                    .permitAll()
                    .anyRequest().authenticated())
            .sessionManagement(sessionManagement -> sessionManagement
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(exceptionHandling -> exceptionHandling
                    .authenticationEntryPoint((request, response, authException) -> {
                      sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Unauthorized");
                    })
                    .accessDeniedHandler((request, response, accessDeniedException) -> {
                      sendErrorResponse(response, HttpStatus.FORBIDDEN, "Access Denied");
                    })
                    .defaultAuthenticationEntryPointFor((request, response, authException) -> {
                      sendErrorResponse(response, HttpStatus.NOT_FOUND, "Address not found");
                    }, request -> true))
    ;

    return http.build();
  }

  private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
    ApiResponseDto<String> apiResponse = new ApiResponseDto<>(false, message, null);
    response.setStatus(status.value());
    response.setContentType("application/json");
    response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
    AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
    builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    return builder.build();
  }
}
