package com.dat.bookapi.auth;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

// runs BEFORE every controller, once per request
// not a @Component on purpose: SecurityConfig creates it, so it only runs inside Spring Security
public class JwtAuthFilter extends OncePerRequestFilter{
    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService){
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{
        String header = request.getHeader("Authorization"); // "Bearer eyJhbGci..."

        if(header != null && header.startsWith("Bearer ")){
            String token = header.substring(7); // cut off "Bearer "
            try{
                String email = jwtService.extractEmail(token);
                // tell Spring "this request is <email>"
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(email, null, List.of());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            catch(JwtException | IllegalArgumentException e){
                // fake or expired token: stay anonymous, locked routes answer 401
            }
        }

        filterChain.doFilter(request, response); // pass the request on to the next step
    }
}
