package com.ssg.myGallery.common.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TokenUtils {

  private final Key key;

  // 생성자 주입 방식으로 변경 (application.yml 활용 권장)
  public TokenUtils(@Value("${jwt.secret}") String secretKey) {
    try {
      byte[] keyBytes = Decoders.BASE64.decode(secretKey);
      this.key = Keys.hmacShaKeyFor(keyBytes);
    } catch(Exception e) {
      throw new RuntimeException("JWT 비밀키 설정을 확인하세요. Base64 인코딩된 문자열이어야 합니다.");
    }
  }

  // 토큰 생성 (Authentication 객체 기반)
  public String generateToken(Authentication authentication, int expireMinutes) {
    String authorities = authentication.getAuthorities().stream()
            .map(a -> a.getAuthority())
            .collect(Collectors.joining(","));

    long now = (new Date()).getTime();
    Date validity = new Date(now + (1000L * 60 * expireMinutes));

    return Jwts.builder()
            .setSubject(authentication.getName())
            .claim("auth", authorities)
            // 필요하다면 memberId 등 추가 정보 claim에 넣기
            .setExpiration(validity)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
  }


  // 토큰에서 인증 정보 조회
  public Authentication getAuthentication(String accessToken) {
    Claims claims = parseClaims(accessToken);

    if (claims.get("auth") == null) {
      throw new RuntimeException("권한 정보가 없는 토큰입니다.");
    }

    // 클레임에서 권한 정보 가져오기
    List<SimpleGrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

    UserDetails principal = new User(claims.getSubject(), "", authorities);
    return new UsernamePasswordAuthenticationToken(principal, "", authorities);
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (Exception e) {
      // 로깅 처리
    }
    return false;
  }

  private Claims parseClaims(String accessToken) {
    try {
      return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
    } catch (ExpiredJwtException e) {
      return e.getClaims();
    }
  }
}