package com.ssg.myGallery.common.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HttpUtils {

  // 쿠키 입력 : 쿠키를 입력하는 메서드, 매개변수로 HTTP 응답 객체와 쿠키의 이름, 값,
  //             유효시간(초)를 받고, HttpOnly 옵션을 true 로 설정하여 해당 쿠키를 서버에서만 접근 가능하도록 설정
  //             매개변수로 받은 유효시간이 0 이하라면 유효시간을 지정하지 않는데
  //             이렇게 하면 웹 브라우저가 종료될 때  쿠키도 삭제됨
  public static void setCookie(HttpServletResponse res, String name, String value, int expSeconds) { // ③
    Cookie cookie = new Cookie(name, value);
    cookie.setHttpOnly(true);       // 자바스크립트(document.cookie)로 접근 불가 -> XSS 공격으로 토큰 탈취 방지
    // cookie.setSecure(true);     // HTTPS 환경에서만 전송 (로컬 개발 시 주석 처리)
    cookie.setPath("/");

    if (expSeconds > 0) {
      cookie.setMaxAge(expSeconds);
    }

    res.addCookie(cookie);
  }

  // ④ 쿠키 값 조회 : 특정 쿠키의 값을 조회하는 메서드. 매개변수로 HTTP 요청 객체와 쿠키의 이름을 받습니다.
  // 역할: 클라이언트가 보내온 요청에서 특정 이름의 쿠키 값을 꺼내는 메서드입니다.
  // 주 사용처: 토큰 재발급(Refresh) 요청 시, 브라우저가 몰래 보내준 리프레시 토큰을 서버가 꺼내볼 때 사용합니다.
  public static String getCookieValue(HttpServletRequest req, String name) {
    Cookie[] cookies = req.getCookies();

    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals(name)) {
          return cookie.getValue();
        }
      }
    }

    return null;
  }

  // ⑤ 쿠키 삭제 : 특정 쿠키 삭제 메서드. 매개변수로 HTTP 요청객체와 쿠키 이름을 받아 삭제합니다.
  // 주 사용처: 로그아웃 시, 저장되어 있던 리프레시 토큰을 삭제하여 더 이상 토큰 재발급을 못 하게 만들 때 사용합니다.
  public static void removeCookie(HttpServletResponse res, String name) {
    Cookie cookie = new Cookie(name, null);
    cookie.setPath("/");
    cookie.setMaxAge(0);
    res.addCookie(cookie);
  }

  // 역할: HTTP 요청 헤더(Authorization)에서 JWT 액세스 토큰을 추출하는 메서드입니다.
  // 주 사용처: **JwtAuthenticationFilter**에서 모든 API 요청마다 사용자의 신분증(액세스 토큰)을 검사하기 위해 꺼낼 때 사용됩니다.
  public static String getBearerToken(HttpServletRequest req) {
    // 헤더에서 "Authorization" 값 가져오기
    String authorization = req.getHeader("Authorization");

    // 값이 있고, "Bearer "로 시작하는지 검사 (RFC 표준 포맷)
    if (authorization != null && authorization.startsWith("Bearer ")) {
      // "Bearer " (7글자)를 잘라내고 순수 토큰 값만 반환
      return authorization.substring(7).trim();
    }

    return null;
  }

}

