package com.mcptest.mcptoy.account.helper;

import com.mcptest.mcptoy.account.dto.AccountJoinRequests;
import com.mcptest.mcptoy.account.dto.AccountLoginRequests;
import com.mcptest.mcptoy.account.etc.AccountConstants;
import com.mcptest.mcptoy.common.util.HttpUtils;
import com.mcptest.mcptoy.member.dto.MemberLogin;
import com.mcptest.mcptoy.member.entity.Member;
import com.mcptest.mcptoy.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionAccountHelper implements AccountHelper{

  private final MemberService memberService;

  @Override
  public void join(AccountJoinRequests joinReq) {
    memberService.save(joinReq.getName(), joinReq.getLoginId(), joinReq.getLoginPw());
  }

  @Override
  public MemberLogin login(AccountLoginRequests loginRequests, HttpServletRequest request,
      HttpServletResponse response) {
    Member member = memberService.find(loginRequests.getLoginId(), loginRequests.getLoginPw());

    if (member == null) {
      return null;
    }
    // 계정을 유지시켜주자 => 세션에 저장
    HttpUtils.setSession(request, AccountConstants.MEMBER_ID_NAME, member.getId());
    // DTO 객체를 생성하여 반환
    return MemberLogin.builder()
        .id(member.getId())
        .loginId(member.getLoginId())
        .name(member.getName())
        .build();
  }


  // 회원 아이디 조회
  @Override
  public Integer getMemberId(HttpServletRequest request) {
    Object memberId = HttpUtils.getSession(request, AccountConstants.MEMBER_ID_NAME);
    if (memberId != null) { return (int) memberId; }
    return null;
  }

  @Override
  // 반환 타입을 MemberLogin DTO로 변경
  public MemberLogin getLoginUser(HttpServletRequest request) {
    Integer memberId = getMemberId(request);

    if (memberId == null) {
      return null; // 세션에 ID가 없으면 null 반환 (로그인 상태 아님)
    }

    // 💡 1. 세션 ID로 DB에서 Member 엔티티를 조회합니다.
    Member member = memberService.findById(memberId);

    if (member == null) {
      // ID는 있으나 DB에 해당 회원이 없으면 세션 무효화 후 null 반환
      HttpUtils.removeSession(request, AccountConstants.MEMBER_ID_NAME);
      return null;
    }

    // 💡 2. 조회된 Member 엔티티를 DTO로 변환하여 반환합니다.
    return MemberLogin.builder()
        .id(member.getId())
        .loginId(member.getLoginId())
        .name(member.getName())
        .build();
  }


  // 로그인 여부 확인
  @Override
  public boolean isLoggedIn(HttpServletRequest request) {
    return getMemberId(request) != null;
  }

  // 로그아웃 처리 : 세션에서 제거
  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response) {
    HttpUtils.removeSession(request, AccountConstants.MEMBER_ID_NAME);
  }
}