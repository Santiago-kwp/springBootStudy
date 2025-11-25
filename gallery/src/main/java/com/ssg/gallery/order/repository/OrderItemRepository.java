package com.ssg.gallery.order.repository;

import com.ssg.gallery.order.entity.OrderItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> { // ①

  // 주문 상품 목록 조회
  List<OrderItem> findAllByOrderId(Integer orderId); // ②
}
