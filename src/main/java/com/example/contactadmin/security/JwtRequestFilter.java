package com.example.contactadmin.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
import java.util.Collections;
import java.util.List;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private JwtUtil jwtUtil;

    @Autowired
    public void setJwtUtil(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestPath= request.getRequestURI();
        if (requestPath.equals("/auth/login") || requestPath.equals("/api/users/register") || requestPath.equals("/api/contacts")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");

        if(authHeader==null || !authHeader.startsWith("Bearer ")){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid JWT token");
            return;
        }

        try{

            String token = authHeader.substring(7);
            /* will throw exception if not valid */
            jwtUtil.validateToken(token);

            Claims claims = jwtUtil.extractClaims(token);

            /* extract information from the token */
            String username = claims.getSubject();
            Long userId = claims.get("userId", Long.class);
            String email = claims.get("email", String.class);
            String role = claims.get("role", String.class);
            System.out.println(role);
            UserPrinciple principal = UserPrinciple.builder()
                    .userId(userId)
                    .username(username)
                    .email(email)
                    .role(role)
                    .build();

            /* make Authentication object */
            List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(principal, null, authorities);
            /*UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(principal, null, Collections.emptyList());*/

            /* set Authentication object to SC*/
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }catch (Exception e){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid JWT token");
            return;
        }
        // Continue the filter chain if valid
        filterChain.doFilter(request, response);
    }
}


//package com.example.contactadmin;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.impl.DefaultClaims;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Base64;
//
//@Component
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    private final String SECRET_KEY = "your_secret_key"; // Use a strong key in production
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain chain) throws ServletException, IOException {
//
//        String authorizationHeader = request.getHeader("Authorization");
//        System.out.println(authorizationHeader);
//
//        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
//            String token = authorizationHeader.substring(7);
//            Claims claims = validateToken(token);
//            System.out.println(claims);
//
//            if (claims != null) {
//                String username = claims.getSubject(); // Extract email
//                System.out.println(username);
//                String role = claims.get("role", String.class); // Extract role
//                Integer userId = claims.get("user_id", Integer.class); // Extract user_id
//
//                UsernamePasswordAuthenticationToken authentication =
//                        new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
//                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            }
//        }
//
//        chain.doFilter(request, response);
//    }
//
//    /*private Claims validateToken(String token) {
//        try {
//            return Jwts.parserBuilder()
//                    .setSigningKey(SECRET_KEY.getBytes())
//                    .build()
//                    .parseClaimsJws(token)
//                    .getBody();
//        } catch (Exception e) {
//            return null; // Invalid token
//        }
//    }*/
//    private Claims validateToken(String token) {
//        try {
//            // Split the JWT into its three parts (header, payload, signature)
//            String[] parts = token.split("\\.");
//            if (parts.length == 3) {
//                // Decode the payload (second part of the JWT)
//                String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
//
//                // Parse the payload into a Claims object
//                ObjectMapper objectMapper = new ObjectMapper();
//                return objectMapper.readValue(payload, DefaultClaims.class); // Convert to Claims object
//            } else {
//                throw new IllegalArgumentException("Invalid JWT format");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null; // Return null if extraction fails
//        }
//    }
//
//}
//
