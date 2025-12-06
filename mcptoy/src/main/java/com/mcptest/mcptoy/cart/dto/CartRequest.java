package com.mcptest.mcptoy.cart.dto;

import com.mcptest.mcptoy.cart.entity.Cart;
import lombok.Getter;

//CartRequest : 장바구니에 데이터 입력을 요청시 사용
@Getter // ①
public class CartRequest {
  private Integer itemId; // ②

  public Cart toEntity(Integer memberId) { // ③
    return new Cart(memberId, itemId);
  }
}
