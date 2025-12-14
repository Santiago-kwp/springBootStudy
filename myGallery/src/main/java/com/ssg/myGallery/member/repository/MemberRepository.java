package com.ssg.myGallery.member.repository;

import com.ssg.myGallery.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Integer> {

  // 아이디로 회원 정보 조회
  Optional<Member> findByLoginId(String loginId);

  // 아이디가 있는지 조회
  Boolean existsByLoginId(String loginId);

}
