package com.api.facturas.security.jwt;

import com.api.facturas.security.CustomerDetailService;
import com.auth0.jwt.interfaces.Claim;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CustomerDetailService customerDetailService;
    private String username = null;
    private Map<String,Claim> claims;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse
            response, FilterChain filterChain) throws ServletException, IOException {
        //Si la ruta (endpoint) coincida con alguna de estas opciones se aplicara esta funcion
        if(request.getServletPath().matches("/user/login|user/forgotPassword|user/signUp|user/getAll|user/edit/")) {
            filterChain.doFilter(request,response);
        } else {
            String token = "";
            //Obtenemos el token del header
            String authorizationHeader = request.getHeader("Authorization");
            if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                token = authorizationHeader.substring(7);
                username = jwtUtil.extractUserName(token);
                claims = jwtUtil.extractAllClaims(token);
            }
            //Si este usuario dado el username aun no esta autenticado
            if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = customerDetailService.loadUserByUsername(username);
                if(jwtUtil.validToken(token,userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                    new WebAuthenticationDetailsSource().buildDetails(request);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
            filterChain.doFilter(request,response);
        }
    }
    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(claims.get("Rol").asString());
    }
    public Boolean isUser() {
        return "user".equalsIgnoreCase(claims.get("Rol").asString());
    }
    public String getCurrentUser() {
        return username;
    }
}
