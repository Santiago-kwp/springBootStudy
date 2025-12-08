package com.mcptest.mcptoy.order.dto;

import com.mcptest.mcptoy.item.dto.ItemRead;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class OrderRead {

  private Integer id;
  private String name; // 주문자명
  private String address;
  private String payment;
  private Long amount;
  private LocalDateTime created;
  private List<OrderItemRead> orderItems; // 기존 : List<OrderRead> items

  @Getter
  @Setter
  @Builder
  public static class OrderItemRead { // ✅ static 내부 클래스로 정의

    private ItemRead item; // 상품 자체의 정보 (기존 ItemRead)
    private Integer qty;   // 주문한 수량
    private Long totalItemAmount; // 주문한 아이템별 총액
  }


}
