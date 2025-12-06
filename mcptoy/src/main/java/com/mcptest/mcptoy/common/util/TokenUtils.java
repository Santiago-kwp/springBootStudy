package com.mcptest.mcptoy.common.util;

import io.jsonwebtoken.*;
import org.springframework.util.StringUtils;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TokenUtils {

  private static final Key signKey; // ① 서명 키로 사용될 필드

  static { // ② TokenUtils 초기화 블록으로 유틸리트 클래스가 처음 로드될때 실행되는 정적 초기화 블록
    //     문자열 secreKey를 생성하고 입력한 다음, 이를 변환해서 서명키 signKey에 입력한다.
    //     secreKey는 발급자(개발자)가 임의로 설정한 32바이트 이상 문자열로, 보안상
    //     부에 노출되면 안 되는 중요한 보안 키, 만약 실제 서비스시 다른 값을 사용해야 한다.

    String secretKey = "SECURITY_KEY_2023042319572107_!!";
    byte[] secretKeyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
    signKey = new SecretKeySpec(secretKeyBytes, SignatureAlgorithm.HS256.getJcaName());
  }

  // 토큰 발급하는 메서드, 매개변수로 토큰의 제목,이름, 값, 유효 시간(분)을 받고 토큰을 생성하고 값을 리턴한다.
  public static String generate(String subject, String name, Object value, int expMinutes) { // ③
    // 만료 시간 설정
    Date expTime = new Date();

    // 분(minute)을 밀리초(millisecond)로 변환해 입력
    expTime.setTime(expTime.getTime() + 1000L * 60 * expMinutes);

    // 기본 정보 입력
    HashMap<String, Object> headerMap = new HashMap<>();
    headerMap.put("typ", "JWT");
    headerMap.put("alg", "HS256");

    // 클레임 입력
    HashMap<String, Object> claims = new HashMap<>();
    claims.put(name, value);

    // 토큰 발급
    JwtBuilder builder = Jwts.builder()
        .setHeader(headerMap)
        .setSubject(subject)
        .setExpiration(expTime)
        .addClaims(claims)
        .signWith(signKey, SignatureAlgorithm.HS256);

    return builder.compact();
  }

  public static boolean isValid(String token) { // ④ 토큰의 상태가 유효한지 확인하는 메서드로 매개변수로 토큰을 받는다.
    // 토큰 값이 있다면
    if (StringUtils.hasLength(token)) {
      try {
        Jwts.parserBuilder().setSigningKey(signKey).build().parseClaimsJws(token);
        return true;
      } catch (ExpiredJwtException e) { // 만료됨
      } catch (JwtException e) { // 유효하지 않음
      }
    }

    return false;
  }

  // 토큰 값 추출 : 토큰의 내부 값을 조회하는 메서드, 매개변수로 받은 토큰을 파싱해서 내부값을 맵(Map)타입으로 리턴한다.
  public static Map<String, Object> getBody(String token) { // ⑤
    return Jwts.parserBuilder().setSigningKey(signKey).build().parseClaimsJws(token).getBody();
  }
}
