package com.mcptest.mcptoy.order.repository;

import com.mcptest.mcptoy.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {

  // 회원의 주문 목록 조회 - 페이지네이션
  Page<Order> findAllByMemberIdOrderByIdDesc(Integer memberId, Pageable pageable); // ②

  // 회원의 주문 정보 상세 조회(주문 한건)
  Optional<Order> findByIdAndMemberId(Integer id, Integer memberId); // ③
}
