package com.ssg.myGallery.account.helper;

import com.ssg.myGallery.account.dto.AccountJoinRequests;
import com.ssg.myGallery.account.dto.AccountLoginRequests;
import com.ssg.myGallery.member.dto.MemberLogin;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AccountHelper {

  // 회원가입 처리 메소드,
  void join(AccountJoinRequests joinReq);

  // 로그인 - 수정
  MemberLogin login(AccountLoginRequests loginRequests,  HttpServletRequest request,  HttpServletResponse response);

  // 회원 아이디 조회
  Integer getMemberId(HttpServletRequest request);

  // 로그인 여부 확인
  boolean isLoggedIn(HttpServletRequest request);

  // 로그아웃
  void logout(HttpServletRequest request, HttpServletResponse response);

}

