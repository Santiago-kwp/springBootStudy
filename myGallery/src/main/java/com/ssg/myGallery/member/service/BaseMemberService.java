package com.ssg.myGallery.member.service;

import com.ssg.myGallery.member.entity.Member;
import com.ssg.myGallery.member.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BaseMemberService implements MemberService{

  private final MemberRepository memberRepository;

  public void save(String name, String loginId, String loginPw) {
    memberRepository.save(new Member(name, loginId, loginPw));
  }

  public Member find(String loginId, String loginPw) {
    Optional<Member> member = memberRepository.findByLoginIdAndLoginPw(loginId, loginPw);
    return member.orElse(null);
  }

  public Member findById(Integer id) {
    Optional<Member> member =  memberRepository.findById(id);
    return member.orElse(null);
  }
}
