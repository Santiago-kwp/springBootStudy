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

@Slf4j
@Service // ① 스프링 컨테이너 서비스 컴포넌트
@Primary
// ② 구현체의 우선순위 애너테이션으로 AccountHelper 인터페이스의 구현체는 2개가 된다.(SessionAccountHelper, TokenAccountHelper) 이때 해당 구현체를 우선적으로 의존성 주입한다.
@RequiredArgsConstructor // ③ 생성자 의존성 주입
public class TokenAccountHelper implements AccountHelper {

  private final MemberService memberService; // ④ 회원 서비스
  private final BlockService blockService; // ⑤ 토큰 차단 서비스
  private final AuthenticationManager authenticationManager; // 주입 필요
  private final TokenUtils tokenUtils; // 주입 필요

  // 액세스 토큰 조회
  private String getAccessToken(HttpServletRequest req) { // ⑥ HTTP 유틸을 호출하여 사용자의 요청에 담긴 토큰을 조회하고 리턴한다.
    return HttpUtils.getBearerToken(req);
  }

  // 리프레시 토큰 조회
  private String getRefreshToken(HttpServletRequest req) { // ⑦ 리프레시 토큰 조회메서드, HTTP 유틸을 호웇라형 쿠키에 담긴 리프레시 토큰을 조회하고 이를 리턴한다.
    return HttpUtils.getCookieValue(req, AccountConstants.REFRESH_TOKEN_NAME);
  }

  // 회원 아이디 조회 (수정됨)
  // TokenUtils가 memberId를 직접 클레임에 넣지 않고 subject(loginId)만 넣으므로 로직 변경
  private Integer getMemberId(String token) {
    if (tokenUtils.validateToken(token)) {
      // 1. 토큰에서 인증 객체 추출
      Authentication authentication = tokenUtils.getAuthentication(token);
      // 2. 로그인 아이디(Subject) 추출
      String loginId = authentication.getName();
      // 3. DB에서 ID 조회
      return memberService.findByLoginId(loginId)
              .map(Member::getId)
              .orElse(null);
    }
    return null;
  }

  // 회원가입
  @Override
  public void join(AccountJoinRequests joinReq) {
    // 1. 이메일(loginId) 중복 검사
    if (memberService.isLoginIdExists(joinReq.getLoginId())) {
      throw new LoginIdDuplicateException("이미 사용 중인 이메일(loginId)입니다: " + joinReq.getLoginId());
    }
    // 2. 저장 (비밀번호 암호화는 MemberService 내부에서 수행한다고 가정)
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

    // 3. 액세스 토큰 발급
    String accessToken = tokenUtils.generateToken(authentication, AccountConstants.ACCESS_TOKEN_EXP_MINUTES);

    // 4. 리프레시 토큰 발급 (같은 인증 정보 사용, 만료 시간만 길게)
    String refreshToken = tokenUtils.generateToken(authentication, AccountConstants.REFRESH_TOKEN_EXP_MINUTES);

    // 5. 리프레시 토큰 쿠키 저장
    HttpUtils.setCookie(res, AccountConstants.REFRESH_TOKEN_NAME, refreshToken, 0);

    // 6. 응답 DTO 생성
    // Authentication 의 Principal 은 CustomUserDetails 타입임
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    Member member = userDetails.getMember();

    return MemberLogin.builder()
            .id(member.getId())
            .loginId(member.getLoginId())
            .name(member.getName())
            .accessToken(accessToken)
            .build();
  }

  // 회원 아이디 조회
  @Override
  public Integer getMemberId(HttpServletRequest req) { // ⑨
    // 액세스 토큰으로 회원 아이디 조회
    return this.getMemberId(getAccessToken(req));
  }

  // 로그인 여부 확인
  @Override
  public boolean isLoggedIn(HttpServletRequest req) { // ⑨
    String accessToken = getAccessToken(req);

    // 1. 액세스 토큰 검증 (인스턴스 메서드 사용)
    if (accessToken != null && tokenUtils.validateToken(accessToken)) {
      return true;
    }

    // 2. 리프레시 토큰 검증
    String refreshToken = getRefreshToken(req);
    return refreshToken != null
            && tokenUtils.validateToken(refreshToken)
            && !blockService.has(refreshToken);
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
}