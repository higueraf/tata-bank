package com.tata.account.security;

import com.tata.account.dto.DataSessionDto;
import com.tata.account.entity.User;
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