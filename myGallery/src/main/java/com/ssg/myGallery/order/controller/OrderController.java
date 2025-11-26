package com.ssg.myGallery.order.controller;

import com.ssg.myGallery.account.helper.AccountHelper;
import com.ssg.myGallery.order.dto.OrderRead;
import com.ssg.myGallery.order.dto.OrderRequest;
import com.ssg.myGallery.order.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class OrderController {

  private final AccountHelper accountHelper; // ④
  private final OrderService orderService;

  // 회원의 주문 목록 전체 가져오기
  @GetMapping("/api/orders")
  public ResponseEntity<?> readAll(HttpServletRequest req) { // ⑥
    // 로그인 회원 아이디
    Integer memberId = accountHelper.getMemberId(req);

    // 주문 목록
    List<OrderRead> orders = orderService.findAll(memberId);

    return new ResponseEntity<>(orders, HttpStatus.OK);
  }

  // 회원의 주문 상세 내역 가져오기
  @GetMapping("/api/orders/{id}")
  public ResponseEntity<?> read(HttpServletRequest req, @PathVariable Integer id) { // ⑦
    // 로그인 회원 아이디
    Integer memberId = accountHelper.getMemberId(req);

    // 주문 조회
    OrderRead order = orderService.find(id, memberId);

    if (order == null) { // 주문 데이터가 없는 경우
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    return new ResponseEntity<>(order, HttpStatus.OK);
  }

  // 회원이 주문하기
  @PostMapping("/api/orders")
  public ResponseEntity<?> add(HttpServletRequest req, @RequestBody OrderRequest orderReq) { // ⑧
    // 로그인 회원 아이디
    Integer memberId = accountHelper.getMemberId(req);

    // 주문 입력
    orderService.order(orderReq, memberId);

    return new ResponseEntity<>(HttpStatus.OK);
  }

}

