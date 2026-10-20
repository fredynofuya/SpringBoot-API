//package com.api.Citaya.security;
//
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Service;
//
//import javax.crypto.SecretKey;
//import java.nio.charset.StandardCharsets;
//import java.util.Date;
//
//@Service
//public class JwtService {
//
//    @Value("${jwt.secret}")
//    private String secret;
//
//    @Value("${jwt.expiration-ms}")
//    private long expirationMs;
//
//    private SecretKey getKey() {
//        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
//    }
//
//    public String generarToken(String email, String rol) {
//        Date ahora = new Date();
//        Date expira = new Date(ahora.getTime() + expirationMs);
//
//        return Jwts.builder()
//                .subject(email)
//                .claim("rol", rol)
//                .issuedAt(ahora)
//                .expiration(expira)
//                .signWith(getKey())
//                .compact();
//    }
//
//    public String extraerEmail(String token) {
//        return parseClaims(token).getSubject();
//    }
//
//    public boolean esValido(String token, UserDetails userDetails) {
//        String email = extraerEmail(token);
//        return email.equals(userDetails.getUsername()) && !estaExpirado(token);
//    }
//
//    private boolean estaExpirado(String token) {
//        return parseClaims(token).getExpiration().before(new Date());
//    }
//
//    private io.jsonwebtoken.Claims parseClaims(String token) {
//        return Jwts.parser()
//                .verifyWith(getKey())
//                .build()
//                .parseSignedClaims(token)
//                .getPayload();
//    }
//}

package com.api.Citaya.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // "v" = versión de sesión del usuario: al cambiar la contraseña sube y los tokens anteriores caducan.
    public String generarToken(String email, String rol, int version) {
        Date ahora = new Date();
        Date expira = new Date(ahora.getTime() + expirationMs);

        return Jwts.builder()
                .subject(email)
                .claim("rol", rol)
                .claim("v", version)
                .issuedAt(ahora)
                .expiration(expira)
                .signWith(getKey())
                .compact();
    }

    public String extraerEmail(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean esValido(String token, UserDetails userDetails) {
        Claims claims = parseClaims(token);

        if (!claims.getSubject().equals(userDetails.getUsername())) return false;
        if (claims.getExpiration().before(new Date())) return false;

        Integer enToken = claims.get("v", Integer.class); // tokens antiguos no lo traen: cuentan como 0
        int versionToken = enToken == null ? 0 : enToken;
        int versionActual = (userDetails instanceof UsuarioPrincipal p) ? p.getVersionToken() : 0;

        return versionToken == versionActual;
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}