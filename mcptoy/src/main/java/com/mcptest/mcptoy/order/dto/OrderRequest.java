package com.mcptest.mcptoy.order.dto;

import com.mcptest.mcptoy.order.entity.Order;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequest {

  private String name;
  private String address;
  private String payment;
  private String cardNumber;
  private Long amount;
  private List<OrderItemRequest> items;

  // OrderItemRequest 내부 클래스 정의 (외부 파일로 분리해도 됨)
  @Getter
  @Setter
  public static class OrderItemRequest {
    private Integer itemId;
    private Integer qty;
  }

  // 엔티티 객체로 변환
  public Order toEntity(Integer memberId) { // ⑨
    return new Order(memberId, name, address, payment, cardNumber, amount);
  }
}
