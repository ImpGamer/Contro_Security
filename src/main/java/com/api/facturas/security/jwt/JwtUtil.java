package com.api.facturas.security.jwt;

import com.api.facturas.dto.UserCredentialDTO;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {
    private final String SECRET = "SpringBoot";
    long fechaCreacion;
    long fechaExpiracion;
    public String createToken(UserCredentialDTO userCredentials,String rol) {
        ZonedDateTime fechaToken = ZonedDateTime.now();
        //Fecha actual formateada a formato UNIX para ser leida por el JWT
        fechaCreacion = fechaToken.toInstant().getEpochSecond();
        fechaExpiracion = fechaCreacion+3600;

        String token;

        try {
            Algorithm algoritmoAplicado = Algorithm.HMAC256(SECRET);

            token = JWT.create()
                    .withIssuer("my-application")
                    .withSubject(userCredentials.getEmail())
                    .withIssuedAt(new Date(fechaCreacion*1000))
                    .withExpiresAt(new Date(fechaExpiracion*1000))
                    //Creacion de un atributo extra dentro del Payload del JWT
                    .withClaim("Rol",rol)
                    .sign(algoritmoAplicado);
        }catch (JWTCreationException ex) {
            ex.printStackTrace();
            return "Error al crear el token D:";
        }
        return token;
    }
    public boolean validToken(String token, UserDetails userDetails) {
        String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpiration(token));
    }
    public Date extractExpiration(String token) {
        DecodedJWT decoded = JWT.decode(token);
        return decoded.getExpiresAt();
    }
    public boolean isTokenExpiration(String token) {
        return extractExpiration(token).after(new Date());
    }
    public String extractUserName(String token) {
        DecodedJWT decoded = JWT.decode(token);
        return decoded.getSubject();
    }
    public Map<String, Claim> extractAllClaims(String token) {
        DecodedJWT decoded = JWT.decode(token);
        return decoded.getClaims();
    }

}