package com.ssg.myGallery.cart.entity;

import com.ssg.myGallery.cart.dto.CartRead;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name="carts")
@Getter
public class Cart {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY) // DB의 정책을 가져다가 그대로 쓰겠다.
  private Integer id;

  @Column(nullable = false)
  private Integer memberId;

  @Column(nullable = false)
  private Integer itemId;

  @Column(updatable = false, nullable = false) // 생성일시 필드, null 허용하지 않고 최초값 입력 후 수정되지 못함
  @CreationTimestamp  // 데이터 삽입 시 값이 없다면 현재 시간이 입력되도록 한다.
  private LocalDateTime created;

  public Cart() { // ⑧
  }

  // CartRequest를 위한 생성자
  public Cart(Integer memberId, Integer itemId) { // ⑧
    this.memberId = memberId;
    this.itemId = itemId;
  }

  //엔티티 객체를 장바구니 조회 DTO로 변환하는 메서드
  //빌더를 활용해 필드값을 초기화하고 객체를 생성한다. 장바구니 서비스에서 데이터 조회시 사용
  public CartRead toRead(){
    return CartRead.builder()
        .id(id)
        .itemId(itemId)
        .build();
  }

}

