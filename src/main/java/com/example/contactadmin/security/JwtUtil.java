package com.example.contactadmin.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class JwtUtil {

    private final String SECRET_KEY;

    public JwtUtil(@Value("${SECRET_KEY:mySecretKey}") String secretKey) {
        SECRET_KEY = secretKey;
    }

    /*
    for simplicity, not using custom exception handler, simply throw the exception catch outside
    and set response to unauthorized with message "invalid jwt token"
    */
    public void validateToken(String token) throws Exception {
        try{
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
        }catch(Exception e){
            throw new Exception("Invalid token");
        }
    }

    public  Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

}