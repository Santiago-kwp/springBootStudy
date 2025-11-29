package com.ssg.myGallery.account.controller;

import com.ssg.myGallery.account.dto.AccountJoinRequests;
import com.ssg.myGallery.account.dto.AccountLoginRequests;
import com.ssg.myGallery.account.etc.AccountConstants;
import com.ssg.myGallery.account.helper.AccountHelper;
import com.ssg.myGallery.block.service.BlockService;
import com.ssg.myGallery.common.util.HttpUtils;
import com.ssg.myGallery.common.util.TokenUtils;
import com.ssg.myGallery.exception.AccountNotFoundException;
import com.ssg.myGallery.exception.InvalidPasswordException;
import com.ssg.myGallery.exception.LoginIdDuplicateException;
import com.ssg.myGallery.member.dto.MemberLogin;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
  private final BlockService blockService; // ① 스프링 컨테이너에 의해 의존성 주입될 토큰 차단 서비스 필드

  // 이메일 형식 검증을 위한 정규표현식 (간단 버전)
  private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

  @PostMapping("/api/account/join")
  public ResponseEntity<?> join(@RequestBody AccountJoinRequests joinReq) {

    // 1. 입력 값 길이 검증
    if (!StringUtils.hasLength(joinReq.getName())
        || !StringUtils.hasLength(joinReq.getLoginId())
        || !StringUtils.hasLength(joinReq.getLoginPw())) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    // 2. 이메일 형식 검증 (정규표현식)
    if (!joinReq.getLoginId().matches(EMAIL_REGEX)) {
      // 유효하지 않은 이메일 형식이라면 400 Bad Request 반환
      return new ResponseEntity<>("유효하지 않은 이메일 형식입니다.", HttpStatus.BAD_REQUEST);
    }


    try {
      // 3. 회원가입 로직 실행 (내부에 중복 검증 포함)
      accountHelper.join(joinReq);
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (LoginIdDuplicateException e) {
      // 4. LoginIdDuplicateException 발생 시 409 Conflict 반환
      return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT); // 409
    }
  }

  @PostMapping("/api/account/login")
  public ResponseEntity<?> login(HttpServletRequest req, HttpServletResponse res, @RequestBody AccountLoginRequests loginReq) {
    // 1. 입력 값 존재 및 길이 검증
    if (!StringUtils.hasLength(loginReq.getLoginId())
        || !StringUtils.hasLength(loginReq.getLoginPw())) {
      return new ResponseEntity<>("아이디와 비밀번호를 모두 입력해야 합니다.", HttpStatus.BAD_REQUEST);
    }

    // 2. 이메일 형식 검증 (정규표현식)
    if (!loginReq.getLoginId().matches(EMAIL_REGEX)) {
      return new ResponseEntity<>("유효하지 않은 이메일 형식입니다.", HttpStatus.BAD_REQUEST);
    }

    try {
      MemberLogin output = accountHelper.login(loginReq, req, res);
      return new ResponseEntity<>(output, HttpStatus.OK); // 200 OK

    } catch (AccountNotFoundException e) { // 아이디 존재 안함 : 404 Not Found
      log.info("Login failed: Account not found for loginId: {}", loginReq.getLoginId());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);

    } catch (InvalidPasswordException e) { // 비밀번호 틀린 경우 : 400 Bad Request
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

    } catch (Exception e) { // 그 외 예상치 못한 서버 오류
      log.info("An unexpected error occurred during login for loginId: {}", loginReq.getLoginId(), e);
      return new ResponseEntity<>("로그인 처리 중 서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR); // 500
    }
  }

  @GetMapping("/api/account/check")
  public ResponseEntity<?> check(HttpServletRequest req) {
    return new ResponseEntity<>(accountHelper.isLoggedIn(req), HttpStatus.OK);
  }

  @PostMapping("/api/account/logout")
  public ResponseEntity<?> logout(HttpServletRequest req, HttpServletResponse res) {
    accountHelper.logout(req, res);
    return new ResponseEntity<>(HttpStatus.OK);
  }

// ② 액세스 토큰을 재발급하는 메서드. @GetMapping 애너테이션을 지정하여 HTTP GET요청을 매핑하고, 연결 경로로 /api/account/token 을 지정한다.
//   쿠키의 리프레시 토큰을 조회하고 이 값이 유효하다면 이 토큰을 활용하여 액세스 토큰을 발급하고 리턴한다.

  @GetMapping("/api/account/token")
  public ResponseEntity<?> regenerate(HttpServletRequest req) {
    String accessToken = "";
    String refreshToken = HttpUtils.getCookieValue(req, AccountConstants.REFRESH_TOKEN_NAME);

    // 리프레시 토큰이 유효하다면
    if (StringUtils.hasLength(refreshToken) && TokenUtils.isValid(refreshToken) && !blockService.has(refreshToken)) {
      // 리프레시 토큰의 내부 값 조회
      Map<String, Object> tokenBody = TokenUtils.getBody(refreshToken);

      // 리프레시 토큰의 회원 아이디 조회
      Integer memberId = (Integer) tokenBody.get(AccountConstants.MEMBER_ID_NAME);

      // 액세스 토큰 발급
      accessToken = TokenUtils.generate(AccountConstants.ACCESS_TOKEN_NAME, AccountConstants.MEMBER_ID_NAME, memberId, AccountConstants.ACCESS_TOKEN_EXP_MINUTES);
    }

    return new ResponseEntity<>(accessToken, HttpStatus.OK);
  }



}

