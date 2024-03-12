package org.vk.backend.security;

import io.jsonwebtoken.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.vk.backend.entity.user.User;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTProvider {
    public static final Logger LOG = LoggerFactory.getLogger(JWTProvider.class);

    public String genToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Date now = new Date(System.currentTimeMillis());
        Date expiryDate = new Date(now.getTime() + SecurityConst.EXPIRATION_TIME);

        String userId = Long.toString(user.getId());

        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("id", userId);
        claimsMap.put("username", user.getUsername());

        // Create JWS header as a Map
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("typ", "JWT");
        headerMap.put("alg", "HS512");

        // Encode the header to Base64Url
        String encodedHeader = Base64.getUrlEncoder().encodeToString(new JSONObject(headerMap).toString().getBytes());

        return Jwts.builder()
                .setHeader(headerMap)
                .setSubject(userId)
                .addClaims(claimsMap)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, SecurityConst.SECRET)
                .compact();

    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(SecurityConst.SECRET).build()
                    .parseClaimsJws(token);
            return true;
        }catch (MalformedJwtException |
                ExpiredJwtException |
                UnsupportedJwtException |
                IllegalArgumentException ex) {
            LOG.error(ex.getMessage());
            return false;
        }
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SecurityConst.SECRET).build()
                .parseClaimsJws(token)
                .getBody();
        String id = (String) claims.get("id");
        return Long.parseLong(id);
    }
}
