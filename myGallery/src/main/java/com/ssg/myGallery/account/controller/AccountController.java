package com.ssg.myGallery.account.controller;

import com.ssg.myGallery.account.dto.AccountJoinRequests;
import com.ssg.myGallery.account.dto.AccountLoginRequests;
import com.ssg.myGallery.account.etc.AccountConstants;
import com.ssg.myGallery.account.helper.AccountHelper;
import com.ssg.myGallery.block.service.BlockService;
import com.ssg.myGallery.common.util.HttpUtils;
import com.ssg.myGallery.common.util.TokenUtils;
import com.ssg.myGallery.member.dto.MemberLogin;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1") // 모든 HTTP 메소드의 요청을 매핑하기 위한 애너테이션
public class AccountController {

  private final AccountHelper accountHelper;
  private final BlockService blockService; // ① 스프링 컨테이너에 의해 의존성 주입될 토큰 차단 서비스 필드

  @PostMapping("/api/account/join")
  public ResponseEntity<?> join(@RequestBody AccountJoinRequests joinReq) {
    // 입력 값이 비어 있다면
    if (!StringUtils.hasLength(joinReq.getName()) || !StringUtils.hasLength(joinReq.getLoginId()) || !StringUtils.hasLength(joinReq.getLoginPw())) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    accountHelper.join(joinReq);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/api/account/login")
  public ResponseEntity<?> login(HttpServletRequest req, HttpServletResponse res, @RequestBody AccountLoginRequests loginReq) {
    // 입력 값이 비어 있다면
    if (!StringUtils.hasLength(loginReq.getLoginId()) || !StringUtils.hasLength(loginReq.getLoginPw())) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    MemberLogin output = accountHelper.login(loginReq, req, res);

    if (output == null) { // 로그인 실패 시
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    return new ResponseEntity<>(output, HttpStatus.OK);
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

