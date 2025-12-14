package com.ssg.myGallery.account.helper;

import com.ssg.myGallery.account.dto.AccountJoinRequests;
import com.ssg.myGallery.account.dto.AccountLoginRequests;
import com.ssg.myGallery.account.etc.AccountConstants;
import com.ssg.myGallery.block.service.BlockService;
import com.ssg.myGallery.common.util.HttpUtils;
import com.ssg.myGallery.common.util.TokenUtils;
import com.ssg.myGallery.exception.AccountNotFoundException;
import com.ssg.myGallery.exception.InvalidPasswordException;
import com.ssg.myGallery.exception.LoginIdDuplicateException;
import com.ssg.myGallery.member.dto.CustomUserDetails;
import com.ssg.myGallery.member.dto.MemberLogin;
import com.ssg.myGallery.member.entity.Member;
import com.ssg.myGallery.member.service.MemberService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service // ① 스프링 컨테이너 서비스 컴포넌트
@Primary
@RequiredArgsConstructor // ③ 생성자 의존성 주입
public class TokenAccountHelper implements AccountHelper {

  private final MemberService memberService; // ④ DB 저장/조회 회원 서비스
  private final BlockService blockService; // ⑤ 토큰 블랙리스트 관리
  private final AuthenticationManager authenticationManager; // 인증 담당 (ID/PW 검사)
  private final TokenUtils tokenUtils; // 토큰 생성/검증/파싱


  // 액세스 토큰 조회
  private String getAccessToken(HttpServletRequest req) { // ⑥ HTTP 유틸을 호출하여 사용자의 요청에 담긴 토큰을 조회하고 리턴한다.
    return HttpUtils.getBearerToken(req);
  }

  // 리프레시 토큰 조회
  private String getRefreshToken(HttpServletRequest req) { // ⑦ 리프레시 토큰 조회메서드, HTTP 유틸을 호웇라형 쿠키에 담긴 리프레시 토큰을 조회하고 이를 리턴한다.
    return HttpUtils.getCookieValue(req, AccountConstants.REFRESH_TOKEN_NAME);
  }

  // 회원 아이디 조회 (claim 기반)
  private Integer getMemberId(String token) {
    if (tokenUtils.validateToken(token)) {
      Claims claims = tokenUtils.parseClaims(token);
      return claims.get("memberId", Integer.class);
    }
    return null;
  }

  // 회원가입
  @Override
  public void join(AccountJoinRequests joinReq) {
    if (memberService.isLoginIdExists(joinReq.getLoginId())) {
      throw new LoginIdDuplicateException("이미 사용 중인 이메일(loginId)입니다: " + joinReq.getLoginId());
    }
    memberService.save(joinReq.getName(), joinReq.getLoginId(), joinReq.getLoginPw());
  }

  // 로그인
  @Override
  public MemberLogin login(AccountLoginRequests loginReq, HttpServletRequest req, HttpServletResponse res) {
    // 1. 인증 토큰 생성 (ID/PW)
    UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(loginReq.getLoginId(), loginReq.getLoginPw());

    // 2. 인증 수행 (Spring Security 가 CustomUserDetailsService + PasswordEncoder 를 사용해 검증)
    // 실패 시 BadCredentialsException 발생 (GlobalExceptionHandler 에서 처리 필요)
    Authentication authentication = authenticationManager.authenticate(authenticationToken);

    // Access / Refresh 토큰 발급 (tokenType 구분자 포함)
    String accessToken = tokenUtils.generateToken(authentication, AccountConstants.ACCESS_TOKEN_EXP_MINUTES, "access");
    String refreshToken = tokenUtils.generateToken(authentication, AccountConstants.REFRESH_TOKEN_EXP_MINUTES, "refresh");

    // Refresh Token 쿠키 저장 (분 → 초 변환만 수행)
    HttpUtils.setCookie(res, AccountConstants.REFRESH_TOKEN_NAME, refreshToken, AccountConstants.REFRESH_TOKEN_EXP_MINUTES * 60);

    // 6. 응답 DTO 생성
    // Authentication 의 Principal 은 CustomUserDetails 타입임
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    Member member = userDetails.getMember();

    return MemberLogin.builder()
            .id(member.getId())
            .loginId(member.getLoginId())
            .name(member.getName())
            .role(member.getRole())
            .accessToken(accessToken)
            .build();
  }

  // 회원 아이디 조회
  @Override
  public Integer getMemberId(HttpServletRequest req) {
    // 액세스 토큰으로 회원 아이디 조회
    return this.getMemberId(getAccessToken(req));
  }

  // 로그인 여부 확인 (Access Token만 검증)
  @Override
  public boolean isLoggedIn(HttpServletRequest req) {
    String accessToken = getAccessToken(req);
    return accessToken != null && tokenUtils.validateToken(accessToken);
  }


  // 로그아웃
  @Override
  public void logout(HttpServletRequest req, HttpServletResponse res) { // ⑨
    // 리프레시 토큰 조회
    String refreshToken = getRefreshToken(req);

    // 리프레시 토큰이 있다면
    if (refreshToken != null) {
      // 쿠키에서 삭제
      HttpUtils.removeCookie(res, AccountConstants.REFRESH_TOKEN_NAME);

      // 토큰 차단 (유효한 토큰인 경우에만 차단 목록에 추가)
      if (tokenUtils.validateToken(refreshToken) && !blockService.has(refreshToken)) {
        blockService.add(refreshToken);
      }
    }
  }

  // Access Token 재발급
  @Override
  public String regenerate(HttpServletRequest req) {
    String refreshToken = getRefreshToken(req);

    if (StringUtils.hasLength(refreshToken)
            && tokenUtils.validateToken(refreshToken)
            && !blockService.has(refreshToken)) {

      Claims claims = tokenUtils.parseClaims(refreshToken);

      // Refresh Token인지 확인
      String type = claims.get("type", String.class);
      if (!"refresh".equals(type)) {
        log.warn("잘못된 토큰 타입으로 재발급 시도: {}", type);
        return null;
      }

      // Authentication 객체 생성 후 새 Access Token 발급
      Authentication authentication = tokenUtils.getAuthentication(refreshToken);
      return tokenUtils.generateToken(authentication, AccountConstants.ACCESS_TOKEN_EXP_MINUTES, "access");
    }

    return null; // 유효하지 않거나 차단된 토큰
  }
}