package com.mcptest.mcptoy.member.repository;

import com.mcptest.mcptoy.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {

  // 아이디와 패스워드로 회원 정보를 조회
  Optional<Member> findByLoginIdAndLoginPw(String loginId, String loginPw);

  // 아이디로 회원 정보 조회
  Optional<Member> findByLoginId(String loginId);

  // 아이디가 있는지 조회
  Boolean existsByLoginId(String loginId);

}
