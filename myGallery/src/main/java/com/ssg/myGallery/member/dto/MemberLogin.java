package com.ssg.myGallery.member.dto;

import com.ssg.myGallery.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberLogin {
  private Integer id;
  private String name;
  private String loginId;

  public MemberLogin(Member member) {
    this.id = member.getId();
    this.name = member.getName();
    this.loginId = member.getLoginId();
  }
}
