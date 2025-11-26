package com.ssg.myGallery.member.service;

import com.ssg.myGallery.member.entity.Member;

public interface MemberService {

  // 회원 데이터를 저장하는 메소드
  void save(String name, String loginId, String loginPw);

  // 회원 데이터를 조회하는 메소드, 매개변수로 로그인한다.
  Member find(String loginId, String loginPw);


  Member findById(Integer id);

}