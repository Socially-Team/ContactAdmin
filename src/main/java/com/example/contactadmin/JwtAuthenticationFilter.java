package com.example.contactadmin;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClaims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final String SECRET_KEY = "your_secret_key"; // Use a strong key in production

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");
        System.out.println(authorizationHeader);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            Claims claims = validateToken(token);
            System.out.println(claims);

            if (claims != null) {
                String username = claims.getSubject(); // Extract email
                System.out.println(username);
                String role = claims.get("role", String.class); // Extract role
                Integer userId = claims.get("user_id", Integer.class); // Extract user_id

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(request, response);
    }

    /*private Claims validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null; // Invalid token
        }
    }*/
    private Claims validateToken(String token) {
        try {
            // Split the JWT into its three parts (header, payload, signature)
            String[] parts = token.split("\\.");
            if (parts.length == 3) {
                // Decode the payload (second part of the JWT)
                String payload = new String(Base64.getUrlDecoder().decode(parts[1]));

                // Parse the payload into a Claims object
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(payload, DefaultClaims.class); // Convert to Claims object
            } else {
                throw new IllegalArgumentException("Invalid JWT format");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Return null if extraction fails
        }
    }

}

