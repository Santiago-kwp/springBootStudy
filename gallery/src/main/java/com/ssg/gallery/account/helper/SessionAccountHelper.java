package com.ssg.gallery.account.helper;

import com.ssg.gallery.account.dto.AccountJoinRequests;
import com.ssg.gallery.account.dto.AccountLoginRequests;
import com.ssg.gallery.account.etc.AccountConstants;
import com.ssg.gallery.common.util.HttpUtils;
import com.ssg.gallery.member.entity.Member;
import com.ssg.gallery.member.service.MemberService;
import jakarta.servlet.http.HttpServlet;
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
  public String login(AccountLoginRequests loginRequests, HttpServletRequest request,
      HttpServletResponse response) {
    Member member = memberService.find(loginRequests.getLoginId(), loginRequests.getLoginPw());

    if (member == null) {
      return null;
    }
    // 계정을 유지시켜주자 => 세션에 저장
    HttpUtils.setSession(request, AccountConstants.Member_ID_NAME, member.getId());
    return member.getLoginId();
  }


  // 회원 아이디 조회
  @Override
  public Integer getMemberId(HttpServletRequest request) {
    Object memberId = HttpUtils.getSession(request, AccountConstants.Member_ID_NAME);
    if (memberId != null) { return (int) memberId; }
    return null;
  }

  // 로그인 여부 확인
  @Override
  public boolean isLoggedIn(HttpServletRequest request) {
    return getMemberId(request) != null;
  }

  // 로그아웃 처리 : 세션에서 제거
  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response) {
    HttpUtils.removeSession(request, AccountConstants.Member_ID_NAME);
  }
}
