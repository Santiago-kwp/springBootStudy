package com.ssg.myGallery.order.service;

import com.ssg.myGallery.cart.service.CartService;
import com.ssg.myGallery.item.dto.ItemRead;
import com.ssg.myGallery.item.service.ItemService;
import com.ssg.myGallery.order.dto.OrderRead;
import com.ssg.myGallery.order.dto.OrderRequest;
import com.ssg.myGallery.order.entity.Order;
import com.ssg.myGallery.order.entity.OrderItem;
import com.ssg.myGallery.order.repository.OrderRepository;
import com.ssg.myGallery.util.EncryptionUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BaseOrderService implements OrderService{

  private final OrderRepository orderRepository;
  private final OrderItemService orderItemService;
  private final ItemService itemService;
  private final CartService cartService;

  // 회원의 주문 전체 목록 조회
  @Override
  public List<OrderRead> findAll(Integer memberId) {
    return orderRepository.findAllByMemberIdOrderByIdDesc(memberId).stream().map(Order::toRead).toList();
  }

  // 회원의 주문 내역 단건 상세 조회
  @Override
  public OrderRead find(Integer id, Integer memberId) {
    Optional<Order> orderOptional = orderRepository.findByIdAndMemberId(id, memberId);

    if (orderOptional.isPresent()) {
      // 주문 조회 DTO로 변환
      OrderRead order = orderOptional.get().toRead();

      // 해당 주문에 포함된 주문 상품 목록 전체 조회
      List<OrderItem> orderItems = orderItemService.findAll(order.getId());

      // 주문 상품 목록 전체의 각 상품 아이디를 추출
      List<Integer> orderItemIds = orderItems.stream().map(OrderItem::getItemId).toList();

      // 주문 상품 리스트의 상품 ID에 해당하는 상품 목록을 조회
      List<ItemRead> items = itemService.findAll(orderItemIds);

      // 주문 조회 DTO에 상품 리스트 데이터를 설정(Set)
      order.setItems(items);

      return order;
    }

    return null;
  }

  // 주문 내용 저장
  @Override
  @Transactional
  public void order(OrderRequest orderReq, Integer memberId) {
    // 주문 상품의 최종 결제 금액을 계산
    List<ItemRead> items = itemService.findAll(orderReq.getItemIds());
    long amount = 0L;

    for (ItemRead item : items) {
      amount += item.getPrice() - item.getPrice().longValue() * item.getDiscountPer() / 100;
    }

    // 주문 요청에 최종 결제 금액 입력
    orderReq.setAmount(amount);

    // 결제 수단이 카드일 때 카드 번호 암호화
    if ("card".equals(orderReq.getPayment())) {
      orderReq.setCardNumber(EncryptionUtils.encrypt(orderReq.getCardNumber()));
    }

    // 주문 저장
    Order order = orderRepository.save(orderReq.toEntity(memberId));

    // 주문 상품을 저장할 빈 배열 리스트 생성
    List<OrderItem> newOrderItems = new ArrayList<>();

    // 상품 아이디만큼 주문 상품 추가
    orderReq.getItemIds().forEach((itemId) -> {
      OrderItem newOrderItem = new OrderItem(order.getId(), itemId);
      newOrderItems.add(newOrderItem);
    });

    // 주문 상품 데이터 저장
    orderItemService.saveAll(newOrderItems);

    // 특정회원의 주문한 아이템만 장바구니에서 삭제
    newOrderItems.forEach(orderItem -> cartService.remove(order.getMemberId(), orderItem.getItemId()));


  }
}
