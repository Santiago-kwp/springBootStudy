package com.ssg.myGallery.order.service;

import com.ssg.myGallery.order.dto.OrderRead;
import com.ssg.myGallery.order.dto.OrderRequest;
import java.util.List;

public interface OrderService {
  // 회원 전체 주문 목록 조회
  List<OrderRead> findAll(Integer memberId); // ①

  // 회원 주문 1건 상세 조회
  OrderRead find(Integer id, Integer memberId); // ②

  // 회원 주문 신청
  void order(OrderRequest orderReq, Integer memberId); // ③

}
