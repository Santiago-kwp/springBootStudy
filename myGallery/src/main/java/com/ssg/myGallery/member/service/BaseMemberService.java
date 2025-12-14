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
    // 암호화
    String encodedPw = passwordEncoder.encode(loginPw);

    String role = "ROLE_USER";

    memberRepository.save(new Member(name, loginId, encodedPw, role));
  }

  // 로그인 아이디로 회원 조회
  public Optional<Member> findByLoginId(String loginId) {
    return memberRepository.findByLoginId(loginId);
  }

  // 로그인 아이디 중복 체크
  public boolean isLoginIdExists(String loginId) {
    return memberRepository.existsByLoginId(loginId);
  }

  // ID로 회원 조회
  public Member findById(Integer id) {
    return memberRepository.findById(id).orElse(null);
  }

  @Override
  public void updateMember(Integer id, String name, String loginPw) {
    Member member = memberRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다. id=" + id));

    // 이름 변경
    if (name != null && !name.isBlank()) {
      member.updateName(name);
    }

    // 비밀번호 변경 (암호화)
    if (loginPw != null && !loginPw.isBlank()) {
      String encodedPw = passwordEncoder.encode(loginPw);
      member.updatePassword(encodedPw);
    }

    memberRepository.save(member); // 변경된 엔티티 저장
  }

  @Override
  public void deleteById(Integer id) {
    if (!memberRepository.existsById(id)) {
      throw new IllegalArgumentException("회원이 존재하지 않습니다. id=" + id);
    }
    memberRepository.deleteById(id);
  }

  @Override
  public void changeRole(Integer id, String role) {
    Member member = memberRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다. id=" + id));

    member.updateRole(role);
    memberRepository.save(member);
  }
}