package com.ssg.myGallery.common.util;

import com.ssg.myGallery.member.dto.CustomUserDetails;
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

  // 비밀키 초기화
  public TokenUtils(@Value("${jwt.secret}") String secretKey) {
    try {
      byte[] keyBytes = Decoders.BASE64.decode(secretKey);
      this.key = Keys.hmacShaKeyFor(keyBytes);
    } catch(Exception e) {
      throw new RuntimeException("JWT 비밀키 설정을 확인하세요. Base64 인코딩된 문자열이어야 합니다.");
    }
  }

  // 토큰 생성 (Authentication 객체 기반)
  public String generateToken(Authentication authentication, int expireMinutes, String tokenType) {
    String authorities = authentication.getAuthorities().stream()
            .map(a -> a.getAuthority())
            .collect(Collectors.joining(","));

    long now = (new Date()).getTime();
    Date validity = new Date(now + (1000L * 60 * expireMinutes));

    // CustomUserDetails에서 memberId 추출
    Integer memberId = null;
    String role = null;
    if (authentication.getPrincipal() instanceof CustomUserDetails customUserDetails) {
      memberId = customUserDetails.getMember().getId();
      role = customUserDetails.getMember().getRole();
    }

    return Jwts.builder()
            .setSubject(authentication.getName()) // loginId
            .claim("auth", authorities)        // 권한 정보
            .claim("memberId", memberId)       // DB의 memberId Pk 값
            .claim("role", role)       // DB의 memberId Pk 값
            .claim("type", tokenType)          // access / refresh 구분자
            .setExpiration(validity)              // 만료 시간
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
    } catch (ExpiredJwtException e) {
      System.out.println("토큰이 만료되었습니다: " + e.getMessage());
    } catch (UnsupportedJwtException e) {
      System.out.println("지원하지 않는 JWT 형식입니다: " + e.getMessage());
    } catch (MalformedJwtException e) {
      System.out.println("JWT 구조가 잘못되었습니다: " + e.getMessage());
    } catch (SignatureException e) {
      System.out.println("JWT 서명이 유효하지 않습니다: " + e.getMessage());
    } catch (IllegalArgumentException e) {
      System.out.println("JWT 토큰이 비어있습니다: " + e.getMessage());
    }
    return false;
  }

  public Claims parseClaims(String accessToken) {
    try {
      return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
    } catch (ExpiredJwtException e) {
      return e.getClaims(); // 만료된 토큰이라도 Claims 반환
    }
  }
}