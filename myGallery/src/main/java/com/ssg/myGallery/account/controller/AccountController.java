package com.ssg.myGallery.account.controller;

import com.ssg.myGallery.account.dto.AccountJoinRequests;
import com.ssg.myGallery.account.dto.AccountLoginRequests;
import com.ssg.myGallery.account.helper.AccountHelper;
import com.ssg.myGallery.exception.LoginIdDuplicateException;
import com.ssg.myGallery.member.dto.MemberLogin;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1") // 모든 HTTP 메소드의 요청을 매핑하기 위한 애너테이션
public class AccountController {

  private final AccountHelper accountHelper;


  // 이메일 형식 검증을 위한 정규표현식 (간단 버전)
  private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

  @PostMapping("/api/account/join")
  public ResponseEntity<?> join(@RequestBody AccountJoinRequests joinReq) {

    // 1. 입력 값 길이 검증
    if (!StringUtils.hasLength(joinReq.getName())
        || !StringUtils.hasLength(joinReq.getLoginId())
        || !StringUtils.hasLength(joinReq.getLoginPw())) {
      return ResponseEntity.badRequest().body(Map.of("message", "필수 입력값 누락"));
    }

    if (!joinReq.getLoginId().matches(EMAIL_REGEX)) {
      return ResponseEntity.badRequest().body(Map.of("message", "유효하지 않은 이메일 형식"));
    }
    try {
      accountHelper.join(joinReq);
      return ResponseEntity.ok(Map.of("message", "회원가입 성공"));
    } catch (LoginIdDuplicateException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
    }
  }

  @PostMapping("/api/account/login")
  public ResponseEntity<?> login(HttpServletRequest req, HttpServletResponse res,
                                 @RequestBody AccountLoginRequests loginReq) {
    if (!StringUtils.hasLength(loginReq.getLoginId())
            || !StringUtils.hasLength(loginReq.getLoginPw())) {
      return ResponseEntity.badRequest().body(Map.of("message", "아이디와 비밀번호를 모두 입력해야 합니다."));
    }
    if (!loginReq.getLoginId().matches(EMAIL_REGEX)) {
      return ResponseEntity.badRequest().body(Map.of("message", "유효하지 않은 이메일 형식"));
    }
    try {
      MemberLogin output = accountHelper.login(loginReq, req, res);
      return ResponseEntity.ok(output);
    } catch (BadCredentialsException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
              .body(Map.of("message", "아이디 또는 비밀번호가 일치하지 않습니다."));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(Map.of("message", "로그인 처리 중 서버 오류 발생"));
    }
  }

  @GetMapping("/api/account/check")
  public ResponseEntity<?> check(HttpServletRequest req) {
    boolean loggedIn = accountHelper.isLoggedIn(req);
    return ResponseEntity.ok(Map.of("loggedIn", loggedIn));
  }

  @PostMapping("/api/account/logout")
  public ResponseEntity<?> logout(HttpServletRequest req, HttpServletResponse res) {
    accountHelper.logout(req, res);
    return ResponseEntity.ok(Map.of("message", "로그아웃 성공"));
  }

// ② 액세스 토큰을 재발급하는 메서드. @GetMapping 애너테이션을 지정하여 HTTP GET 요청을 매핑하고, 연결 경로로 /api/account/token 을 지정한다.
//   쿠키의 리프레시 토큰을 조회하고 이 값이 유효하다면 이 토큰을 활용하여 액세스 토큰을 발급하고 리턴한다.

  @GetMapping("/api/account/token")
  public ResponseEntity<?> regenerate(HttpServletRequest req) {
    String newAccessToken = accountHelper.regenerate(req);
    if (newAccessToken == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
              .body(Map.of("message", "토큰이 유효하지 않거나 만료되었습니다."));
    }
    return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
  }



}

