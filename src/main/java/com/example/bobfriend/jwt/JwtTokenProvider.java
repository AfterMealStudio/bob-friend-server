package com.example.bobfriend.jwt;

import com.example.bobfriend.model.dto.TokenDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider implements InitializingBean {
    @Value("${jwt.header}")
    private String AUTHORIZATION_HEADER;
    private static final String AUTHORITY_KEY = "roles";
    private final String secretForAccess, secretForRefresh;
    private Key accessKey, refreshKey;
    @Value("${jwt.access.expire}")
    private int ACCESS_TOKEN_VALID_DAY;
    @Value("${jwt.refresh.expire}")
    private int REFRESH_TOKEN_VALID_DAT;

    public JwtTokenProvider(@Value("${jwt.access.key}") String secretForAccess,
                            @Value("${jwt.refresh.key}") String secretForRefresh) {
        this.secretForAccess = secretForAccess;
        this.secretForRefresh = secretForRefresh;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.accessKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretForAccess));
        this.refreshKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretForRefresh));
    }


    public TokenDto.Token createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime accessTokenValidTime = now.plusMinutes(ACCESS_TOKEN_VALID_DAY);
        LocalDateTime refreshTokenValidTime = now.plusMinutes(REFRESH_TOKEN_VALID_DAT);

        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITY_KEY, authorities)
                .setExpiration(Date.from(accessTokenValidTime.toInstant(ZoneOffset.UTC)))
                .signWith(accessKey, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setExpiration(Date.from(refreshTokenValidTime.toInstant(ZoneOffset.UTC)))
                .signWith(refreshKey, SignatureAlgorithm.HS256)
                .compact();

        return TokenDto.Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(accessKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Collection<SimpleGrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITY_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateAccessToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(accessKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(refreshKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String resolveToken(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION_HEADER);
    }

}
