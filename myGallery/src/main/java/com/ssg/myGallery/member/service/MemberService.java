package com.ssg.myGallery.member.service;

import com.ssg.myGallery.member.entity.Member;
import java.util.Optional;

public interface MemberService {

  // 회원 데이터를 저장하는 메소드
  void save(String name, String loginId, String loginPw);

  // 회원 찾기
  Member findById(Integer id);
  // 회원 정보 수정
  void updateMember(Integer id, String name, String loginPw);
  // 회원 삭제
  void deleteById(Integer id);
  // 회원 권한 수정
  void changeRole(Integer id, String role);
  // 회원 로그인 아이디 존재 여부
  boolean isLoginIdExists(String loginId);
  // 회원 로그인 아이디로 회원 정보 찾기
  Optional<Member> findByLoginId(String loginId);

}