package com.ssg.myGallery.order.repository;

import com.ssg.myGallery.order.entity.OrderItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> { // ①

  // 주문 ID에 해당하는 주문 상품 목록 조회
  List<OrderItem> findAllByOrderId(Integer orderId); // ②
}
