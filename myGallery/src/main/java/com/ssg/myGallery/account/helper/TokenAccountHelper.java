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
import com.ssg.myGallery.member.dto.MemberLogin;
import com.ssg.myGallery.member.entity.Member;
import com.ssg.myGallery.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Slf4j
@Service // ① 스프링 컨테이너 서비스 컴포넌트
@Primary
// ② 구현체의 우선순위 애너테이션으로 AccountHelper 인터페이스의 구현체는 2개가 된다.(SessionAccountHelper, TokenAccountHelper) 이때 해당 구현체를 우선적으로 의존성 주입한다.
@RequiredArgsConstructor // ③ 생성자 의존성 주입
public class TokenAccountHelper implements AccountHelper {

  private final MemberService memberService; // ④ 회원 서비스
  private final BlockService blockService; // ⑤ 토큰 차단 서비스

  // 액세스 토큰 조회
  private String getAccessToken(HttpServletRequest req) { // ⑥ HTTP 유틸을 호출하여 사용자의 요청에 담긴 토큰을 조회하고 리턴한다.
    return HttpUtils.getBearerToken(req);
  }

  // 리프레시 토큰 조회
  private String getRefreshToken(HttpServletRequest req) { // ⑦ 리프레시 토큰 조회메서드, HTTP 유틸을 호웇라형 쿠키에 담긴 리프레시 토큰을 조회하고 이를 리턴한다.
    return HttpUtils.getCookieValue(req, AccountConstants.REFRESH_TOKEN_NAME);
  }

  // 회원 아이디 조회
  private Integer getMemberId(String token) { // ⑧ 토큰을 통해 회원 아이디 조회 메서드  토큰 유틸을 호출하여 매개변수로 받은 토큰에 담긴 회원아이디를 조회하고 이를 리턴한다.
    if (TokenUtils.isValid(token)) {
      Map<String, Object> tokenBody = TokenUtils.getBody(token);
      return (Integer) tokenBody.get(AccountConstants.MEMBER_ID_NAME);
    }

    return null;
  }

  // 회원가입
  @Override
  public void join(AccountJoinRequests joinReq) { // ⑨

    // 1. 이메일(loginId) 중복 검사
    if (memberService.isLoginIdExists(joinReq.getLoginId())) {
      // 중복된 경우, 409 Conflict를 유발할 커스텀 예외 발생
      throw new LoginIdDuplicateException("이미 사용 중인 이메일(loginId)입니다: " + joinReq.getLoginId());
    }

    // 2. 중복이 아니면 저장 처리
    memberService.save(joinReq.getName(), joinReq.getLoginId(), joinReq.getLoginPw());
  }

  // 로그인
  @Override
  public MemberLogin login(AccountLoginRequests loginReq, HttpServletRequest req, HttpServletResponse res) { // ⑨

    // 1. 회원 ID로 회원 정보 조회
    Member member = memberService.findByLoginId(loginReq.getLoginId())
        .orElseThrow(() -> new AccountNotFoundException("존재하지 않는 회원 ID입니다.")); // 404 대응

    // 2. 비밀번호 일치 검증
    if (memberService.find(loginReq.getLoginId(), loginReq.getLoginPw()) == null) {
      throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");
    }

    // 회원 아이디
    Integer memberId = member.getId();

    // 액세스 토큰 발급
    String accessToken = TokenUtils.generate(AccountConstants.ACCESS_TOKEN_NAME, AccountConstants.MEMBER_ID_NAME, memberId, AccountConstants.ACCESS_TOKEN_EXP_MINUTES);

    // 리프레시 토큰 발급
    String refreshToken = TokenUtils.generate(AccountConstants.REFRESH_TOKEN_NAME, AccountConstants.MEMBER_ID_NAME, memberId, AccountConstants.REFRESH_TOKEN_EXP_MINUTES);

    // 리프레시 토큰 쿠키 저장(유효 시간을 0으로 입력해 웹 브라우저 종료 시 삭제)
    HttpUtils.setCookie(res, AccountConstants.REFRESH_TOKEN_NAME, refreshToken, 0);

    // 💡 Builder를 사용하여 Member 엔티티의 데이터를 분리하여 DTO 생성
    return MemberLogin.builder()
        .id(member.getId())             // Member 엔티티에서 ID를 가져옴
        .loginId(member.getLoginId())   // Member 엔티티에서 LoginId를 가져옴
        .name(member.getName())         // Member 엔티티에서 Name을 가져옴
        .accessToken(accessToken)       // 발급된 토큰을 주입
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
    // 액세스 토큰이 유효하다면
    if (TokenUtils.isValid((getAccessToken(req)))) {
      log.info("here??!!");
      return true;
    }

    // 리프레시 토큰 조회
    String refreshToken = getRefreshToken(req);

    // 리프레시 토큰의 유효성 확인
    return TokenUtils.isValid(refreshToken) && !blockService.has(refreshToken);
  }

  @Override
  public MemberLogin getLoginUser(HttpServletRequest request) {
    return null;
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

      // 토큰 차단 데이터에 해당 토큰이 없다면
      if (!blockService.has(refreshToken)) {
        // 차단 토큰으로 추가
        blockService.add(refreshToken);
      }
    }
  }
}