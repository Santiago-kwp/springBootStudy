package com.ssg.myGallery.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Entity
@Table(name="members")
public class Member {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(length = 50, nullable = false)
  private String name;

  @Column(length = 50, nullable = false, unique = true)
  private String loginId;

  @Column(length = 44, nullable = false)
  private String loginPw;

  @Column(length = 16, nullable = false)
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