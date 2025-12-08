package com.mcptest.mcptoy.cart.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartSyncRequest {
    private Long itemId;
    private int itemCount;
}
