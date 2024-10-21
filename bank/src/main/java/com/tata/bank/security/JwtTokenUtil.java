package com.tata.bank.security;

import com.tata.bank.dto.DataSessionDto;
import com.tata.bank.entity.User;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtil {

    private final Dotenv dotenv;

    @Autowired
    public JwtTokenUtil(Dotenv dotenv) {
        this.dotenv = dotenv;
    }

    private String getSecretKey() {
        return dotenv.get("JWT_SECRET_KEY", "defaultSecretKey");
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(getSecretKey()).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(User user) {
        try {
            Map<String, Object> claims = new HashMap<>();
            String roles = user.getUserRoles().stream()
                    .map(userRole -> userRole.getRole().getName().toUpperCase())
                    .collect(Collectors.joining(", "));
            claims.put("roles", roles != null ? roles : "null");
            claims.put("name", user.getName() != null ? user.getName() : "null");
            claims.put("last_name", user.getLastName() != null ? user.getLastName() : "null");
            claims.put("email", user.getEmail() != null ? user.getEmail() : "null");
            claims.put("user_id", user.getId() != null ? user.getId().toString() : "null");
            claims.put("user_phone_number_code", user.getPhoneNumberCode() != null ? user.getPhoneNumberCode() : "null");
            claims.put("user_phone_number", user.getPhoneNumber() != null ? user.getPhoneNumber() : "null");
            return createToken(claims, user.getUsername() != null ? user.getUsername() : "null");
        } catch (Exception e) {
            System.err.println("Error generating token: " + e.getMessage());
            e.printStackTrace();
            return "Error generating token";
        }
    }


    public String createToken(Map<String, Object> claims, String subject) {
        long expirationTime = 1000 * 60 * 60 * 10L;
        try {
            String expirationString = dotenv.get("JWT_EXPIRATION");
            if (expirationString != null) {
                expirationTime = Long.parseLong(expirationString);
            }
        } catch (NumberFormatException e) {
            System.err.println("JWT_EXPIRATION is not a valid number. The default value will be used.");
        }
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(new Date().getTime() + expirationTime))
                .signWith(SignatureAlgorithm.HS512, getSecretKey())
                .compact();
    }

    public DataSessionDto getDataSessionFromToken(String token) {
        Claims claims = extractAllClaims(token);
        DataSessionDto dataSession = new DataSessionDto();
        dataSession.setUserId(claims.get("user_id", String.class));
        dataSession.setCompanyId(claims.get("company_id", String.class));
        dataSession.setLanguage(claims.get("language", String.class));
        dataSession.setEmail(claims.get("sub", String.class));
        dataSession.setRoles(claims.get("roles", String.class));
        dataSession.setName(claims.get("name", String.class));
        dataSession.setLastName(claims.get("last_name", String.class));
        dataSession.setUserPhoneNumber(claims.get("user_phone_number", String.class));
        dataSession.setUserPhoneNumberCode(claims.get("user_phone_number_code", String.class));
        dataSession.setToken(token);
        return dataSession;
    }

}