package com.tata.account.config;

import com.tata.account.dto.DataSessionDto;
import com.tata.account.security.JwtTokenUtil;
import com.tata.account.service.UserDetailsServiceImpl;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenUtil jwtTokenUtil;
    private AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(
            UserDetailsServiceImpl userDetailsService,
            JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Autowired
    private Dotenv dotenv;

    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String requestTokenHeader = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtTokenUtil.extractUsername(jwtToken);
            } catch (IllegalArgumentException e) {
                System.out.println("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                System.out.println("JWT Token has expired");
            }
        } else {
            filterChain.doFilter(request, response);
            return;
        }

        if (new AntPathRequestMatcher("/api/auth/login").matches(request)) {
            username = request.getParameter("username");
            String password = request.getParameter("password");
            if (username != null && password != null) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        username, password);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                Authentication authResult = authenticationManager.authenticate(authToken);
                if (authResult.isAuthenticated()) {
                    SecurityContextHolder.getContext().setAuthentication(authResult);
                    String token = Jwts.builder()
                            .setSubject(username)
                            .setExpiration(
                                    new Date(System.currentTimeMillis() + Long.parseLong(
                                            Objects.requireNonNull(dotenv.get("JWT_EXPIRATION")))))
                            .signWith(SignatureAlgorithm.HS512, dotenv.get(
                                    "JWT_SECRET_KEY",
                                    "defaultSecretKey"))
                            .compact();
                    response.addHeader("Authorization", "Bearer " + token);
                }
            }
            filterChain.doFilter(request, response);
        } else {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.getName().equals(username)) {
                DataSessionDto dataSession = jwtTokenUtil.getDataSessionFromToken(jwtToken);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        dataSession, null, null);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
            filterChain.doFilter(request, response);
        }
    }
}
