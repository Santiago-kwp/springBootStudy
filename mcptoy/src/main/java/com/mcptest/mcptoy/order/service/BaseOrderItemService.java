package com.mcptest.mcptoy.order.service;

import com.mcptest.mcptoy.order.entity.OrderItem;
import com.mcptest.mcptoy.order.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BaseOrderItemService implements OrderItemService{

  private final OrderItemRepository orderItemRepository;

  // 주문 하나에 포함되는 전체 주문 상품 조회
  @Override
  public List<OrderItem> findAll(Integer orderId) {
    return orderItemRepository.findAllByOrderId(orderId);
  }

  // 주문 상품 리스트 저장
  @Override
  public void saveAll(List<OrderItem> orderItems) {
    orderItemRepository.saveAll(orderItems);
  }
}
