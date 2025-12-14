package com.ssg.myGallery.member.service;

import com.ssg.myGallery.member.entity.Member;
import java.util.Optional;

public interface MemberService {

  // 회원 데이터를 저장하는 메소드
  void save(String name, String loginId, String loginPw);


  Member findById(Integer id);

  boolean isLoginIdExists(String loginId);

  Optional<Member> findByLoginId(String loginId);

}