package com.mcptest.mcptoy.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name="members")
public class Member {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(length = 50, nullable = false)
  private String name;

  @Column(name = "login_id", length = 50, nullable = false, unique = true)
  private String loginId;

  @Column(name = "login_pw", length = 44, nullable = false)
  private String loginPw;

  @Column(name = "login_pw_salt", length = 16, nullable = false)
  private String loginPwSalt;

  @Column(updatable = false, nullable = false)
  @CreationTimestamp  // 데이터 삽입 시 값이 없다면 현재 시간이 입력되도록 한다.
  private LocalDateTime created;

  public Member() {}

  public Member(String name, String loginId, String loginPw, String loginPwSalt) {
    this.name = name;
    this.loginId = loginId;
    this.loginPw = loginPw;
    this.loginPwSalt = loginPwSalt;
  }


}