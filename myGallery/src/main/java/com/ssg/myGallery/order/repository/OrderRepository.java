package com.ssg.myGallery.order.repository;

import com.ssg.myGallery.order.entity.Order;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {

  // 회원의 주문 목록 조회 - 페이지네이션
  Page<Order> findAllByMemberIdOrderByIdDesc(Integer memberId, Pageable pageable); // ②

  // 회원의 주문 정보 상세 조회(주문 한건)
  Optional<Order> findByIdAndMemberId(Integer id, Integer memberId); // ③
}
