package com.ssg.myGallery.common.util;

import com.ssg.myGallery.member.dto.CustomUserDetails;
import com.ssg.myGallery.member.entity.Member;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Component
public class TokenUtils {

  private final Key key;

  // 비밀키 초기화
  public TokenUtils(@Value("${jwt.secret}") String secretKey) {
    try {
      // 1. application.properties에서 가져온 문자열을 Base64로 디코딩
      byte[] keyBytes = Decoders.BASE64.decode(secretKey);
      // 2. HMAC-SHA 알고리즘에 사용할 Key 객체로 변환
      this.key = Keys.hmacShaKeyFor(keyBytes);
    } catch(Exception e) {
      throw new RuntimeException("JWT 비밀키 설정을 확인하세요...");
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


  // 토큰에서 인증 객체 복원
  public Authentication getAuthentication(String accessToken) {
    // 1. 토큰 파싱
    Claims claims = parseClaims(accessToken);

    // 2. 권한 정보 유무 확인
    if (claims.get("auth") == null) {
      throw new RuntimeException("권한 정보가 없는 토큰입니다.");
    }

    // 3. 권한 정보 가져오
    List<SimpleGrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

    // 4. Member 객체 임시 생성 (생성자 사용)
    Integer memberId = claims.get("memberId", Integer.class);
    String role = claims.get("role", String.class);
    String loginId = claims.getSubject();

    // Setter 대신 생성자로 주입
    Member member = new Member(memberId, loginId, role);

    // 5. CustomUserDetails 객체 생성
    CustomUserDetails principal = new CustomUserDetails(member);

    // 6. 최종 인증 토큰 반환
    return new UsernamePasswordAuthenticationToken(principal, "", authorities);
  }

  // 역할: 토큰이 위조되었거나, 만료되었거나, 형식이 잘못되었는지 검사합니다.
  public boolean validateToken(String token) {
    try {
      // 서명 키(key)를 이용해 파싱을 시도. 성공하면 true
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (ExpiredJwtException e) {
      log.error("토큰이 만료되었습니다: " + e.getMessage());
    } catch (UnsupportedJwtException e) {
      log.error("지원하지 않는 JWT 형식입니다: " + e.getMessage());
    } catch (MalformedJwtException e) {
      log.error("JWT 구조가 잘못되었습니다: " + e.getMessage());
    } catch (SignatureException e) {
      log.error("JWT 서명이 유효하지 않습니다: " + e.getMessage());
    } catch (IllegalArgumentException e) {
      log.error("JWT 토큰이 비어있습니다: " + e.getMessage());
    }
    return false;
  }

  // 역할: 토큰 내부의 정보(Payload)를 꺼냅니다.
  public Claims parseClaims(String accessToken) {
    try {
      return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
    } catch (ExpiredJwtException e) {
      return e.getClaims(); // 만료된 토큰이라도 Claims 반환
    }
  }
}