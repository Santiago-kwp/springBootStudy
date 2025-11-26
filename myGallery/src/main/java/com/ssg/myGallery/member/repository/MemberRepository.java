package com.ssg.myGallery.member.repository;

import com.ssg.myGallery.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Integer> {

  // 아이디와 패스워드로 회원 정보를 조회
  Optional<Member> findByLoginIdAndLoginPw(String loginId, String loginPw);

}
