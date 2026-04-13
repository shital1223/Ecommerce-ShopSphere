package com.ecom.user.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;


import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {
    //todo: change secret key, pull from env
    private String secretKey = "MishitaShitalGauravMahajanabcdefghi";
    private long validity = 3600000; //(1000 * 60 * 60)

    public String createToken(Authentication authentication){
        Date now = new Date();
        Date expiryDate =  new Date(now.getTime() + validity);
        String jti = UUID.randomUUID().toString();

        List<String> roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        String token =  Jwts.builder()
                .subject(authentication.getName())
                .claim("roles",roles)
                .issuedAt(now)
                .expiration(expiryDate)
                .id(jti)
                .issuer("ecom-app")
                .signWith(key())
                .compact();

        return token;
    }

    private SecretKey key(){
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // claims:
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .requireIssuer("ecom-app")
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUserNameFromToken(String token){
        return getClaims(token).getSubject();
    }

    // Extract the roles from the token
    public List<String> getRolesFromToken(String token) {
        return getClaims(token).get("roles",List.class);
    }
    // get id of the token
    public String getId(String token) {
        return getClaims(token).getId();
    }

    // get expiry date from token
    public Date getExp(String token){
        return getClaims(token).getExpiration();
    }

    public boolean validateToken(String token){
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}
