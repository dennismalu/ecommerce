package it.progetto.ecommerce.services.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component
public class JwtService {

    //chiave per firmare il token
    @Value("${security.jwt.secret-key}")
    private String SECRET;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;  //new Date(System.currentTimeMillis() + 10000 * 60 * 30)) //30 minuti

    private Key getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        //claims è utile se si vogliono aggiungere informazioni aggiuntive al token
        //di default il token ha già memorizzato al suo interno "subject", "issuedAt" e "expiration" (che sono "default claims")
        return createToken(claims, email);
    }

    private String createToken(Map<String, Object> claims, String email) {
        return Jwts.builder()
//                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSecretKey(), SignatureAlgorithm.HS512) //firma il token
                .compact(); //converte il token in stringa
    }

    public String extractEmail(String token) {
//        Claims claims = extractAllClaims(token);
//        return claims.getSubject();
        //return extractClaim(token, Claims::getSubject);
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Date extractExpiration(String token) {
//        Claims claims = extractAllClaims(token);
//        return claims.getExpiration();
        //return extractClaim(token, Claims::getExpiration);
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

//    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//        final Claims claims = extractAllClaims(token);
//        return claimsResolver.apply(claims);
//    }

//    private Claims extractAllClaims(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(getSecretKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String email = extractEmail(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }


}
