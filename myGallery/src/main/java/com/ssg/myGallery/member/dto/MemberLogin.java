package com.ssg.myGallery.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
public class MemberLogin {
  private final Integer id;
  private final String name;
  private final String loginId;
  // JWT 액세스 토큰을 담을 속성 추가
  private final String accessToken;

}
