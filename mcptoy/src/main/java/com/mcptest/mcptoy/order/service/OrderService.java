package com.mcptest.mcptoy.order.service;

import com.mcptest.mcptoy.order.dto.OrderRead;
import com.mcptest.mcptoy.order.dto.OrderRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
  // 회원 전체 주문 목록 조회
  Page<OrderRead> findAll(Integer memberId, Pageable pageable); // ①

  // 회원 주문 1건 상세 조회
  OrderRead find(Integer id, Integer memberId); // ②

  // 회원 주문 신청
  void order(OrderRequest orderReq, Integer memberId); // ③

}
