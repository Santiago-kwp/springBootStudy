package com.ssg.gallery.order.service;


import com.ssg.gallery.order.entity.OrderItem;
import com.ssg.gallery.order.repository.OrderItemRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BaseOrderItemService implements OrderItemService{

  private final OrderItemRepository orderItemRepository;

  @Override
  public List<OrderItem> findAll(Integer orderId) {
    return orderItemRepository.findAllByOrderId(orderId);
  }

  @Override
  public void saveAll(List<OrderItem> orderItems) {
    orderItemRepository.saveAll(orderItems);
  }
}
