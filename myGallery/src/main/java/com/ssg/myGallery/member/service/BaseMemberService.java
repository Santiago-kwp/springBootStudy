package com.ssg.myGallery.member.service;

import com.ssg.myGallery.member.entity.Member;
import com.ssg.myGallery.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BaseMemberService implements MemberService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder; // 주입

  public void save(String name, String loginId, String loginPw) {
    // BCrypt는 Salt를 내부적으로 생성하므로 별도 Salt 관리 불필요

    // 암호화
    String encodedPw = passwordEncoder.encode(loginPw);

    String role = "ROLE_USER";

    memberRepository.save(new Member(name, loginId, encodedPw, role));
  }

  // find(id, pw) 메서드는 이제 Security가 대신하므로 삭제하거나 findByLoginId만 남김
  public Optional<Member> findByLoginId(String loginId) {
    return memberRepository.findByLoginId(loginId);
  }

  public boolean isLoginIdExists(String loginId) {
    return memberRepository.existsByLoginId(loginId);
  }

  public Member findById(Integer id) {
    return memberRepository.findById(id).orElse(null);
  }
}