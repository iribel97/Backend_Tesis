package com.tesis.BackV2.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    // Clave secreta para firmar el token
    private static final String SECRET_KEY = "ojnCnGB4dEE2aMm4yPcG9/2AMXsrRY1Bk81k6jaqNXZ7BID7tx0rRt+xW384hReb";

    // Extraer el nombre de usuario del token
    public String extractUsername(String token) {
        // Devuelve el nombre de usuario del token
        return extractClaim(token, Claims::getSubject);
    }

    // Generar un token
    public String generateToken(UserDetails userDetails) {
        // Genera un token con los detalles del usuario
        return generateToken(new HashMap<>(),userDetails);
    }

    // Generar un token con claims adicionales, como el rol del usuario, etc.
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails){
        return Jwts.builder()
                .setClaims(extraClaims) // Agrega los claims adicionales
                .setSubject(userDetails.getUsername()) // Agrega el nombre de usuario
                .setIssuedAt(new Date(System.currentTimeMillis())) // Agrega la fecha de emisión del token en milisegundos
                .setExpiration(new Date(System.currentTimeMillis()+1000660*24)) // Agrega la fecha de expiración del token en milisegundos
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Firma el token con la clave secreta y el algoritmo de firma
                .compact();
    }

    // Extraer un claim específico del token
    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims); // Devuelve el claim específico
    }

    // Verificar si el token es válido
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Verificar si el token ha expirado
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Extraer la fecha de expiración del token
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extraer todos los claims del token
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(getSignInKey()).parseClaimsJws(token).getBody();
    }

    // Obtener la clave secreta para firmar el token
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
