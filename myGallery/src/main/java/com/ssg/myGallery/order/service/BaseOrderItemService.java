package com.ssg.myGallery.order.service;

import com.ssg.myGallery.order.entity.OrderItem;
import com.ssg.myGallery.order.repository.OrderItemRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
