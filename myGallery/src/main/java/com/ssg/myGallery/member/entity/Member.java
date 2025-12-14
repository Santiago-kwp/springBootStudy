package com.ssg.myGallery.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Entity
@Table(name="members")
@NoArgsConstructor
public class Member {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(length = 50, nullable = false)
  private String name;

  @Column(length = 50, nullable = false, unique = true)
  private String loginId;

  @Column(length = 100, nullable = false)
  private String loginPw;


  @Column(length = 20, nullable = false)
  private String role;  // 예: "ROLE_USER", "ROLE_ADMIN"

  @Column(updatable = false, nullable = false)
  @CreationTimestamp  // 데이터 삽입 시 값이 없다면 현재 시간이 입력되도록 한다.
  private LocalDateTime created;

  @UpdateTimestamp
  private LocalDateTime updated;

  public Member(String name, String loginId, String loginPw, String role) {
    this.name = name;
    this.loginId = loginId;
    this.loginPw = loginPw;
    this.role = role;
  }

  // ★ [추가] TokenUtils에서 객체를 복원할 때 사용할 생성자
  public Member(Integer id, String loginId, String role) {
    this.id = id;
    this.loginId = loginId;
    this.role = role;
    this.loginPw = ""; // 인증 끝났으니 비번은 비워둠
    this.name = "";    // 토큰에 이름이 없다면 빈값 처리 (필요하면 토큰에 name도 넣어서 가져와야 함)
  }

  // 회원 정보 수정을 위한 도메인 메소드
  public void updateName(String name) { this.name = name; }
  public void updatePassword(String encodedPw) { this.loginPw = encodedPw; }
  public void updateRole(String role) { this.role = role; }

}