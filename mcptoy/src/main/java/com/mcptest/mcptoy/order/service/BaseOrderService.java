package com.mcptest.mcptoy.order.service;

import com.mcptest.mcptoy.cart.service.CartService;
import com.mcptest.mcptoy.item.dto.ItemRead;
import com.mcptest.mcptoy.item.service.ItemService;
import com.mcptest.mcptoy.order.dto.OrderRead;
import com.mcptest.mcptoy.order.dto.OrderRequest;
import com.mcptest.mcptoy.order.entity.Order;
import com.mcptest.mcptoy.order.entity.OrderItem;
import com.mcptest.mcptoy.order.repository.OrderRepository;
import com.mcptest.mcptoy.util.EncryptionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BaseOrderService implements OrderService{

  private final OrderRepository orderRepository;
  private final OrderItemService orderItemService;
  private final ItemService itemService;
  private final CartService cartService;

  // 회원의 주문 전체 목록 조회
  @Override
  public Page<OrderRead> findAll(Integer memberId, Pageable pageable) {
    Page<Order> orders = orderRepository.findAllByMemberIdOrderByIdDesc(memberId, pageable);
    return orders.map(Order::toRead);
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

      // Item ID를 key로 ItemRead를 빠르게 찾기 위한 Map 생성
      var itemMap = items.stream()
              .collect(Collectors.toMap(ItemRead::getId, item -> item));

      // 5. OrderItem 엔티티와 ItemRead를 결합하여 OrderRead.OrderItemRead DTO 구성
      List<OrderRead.OrderItemRead> orderItemReads = new ArrayList<>();

      for (OrderItem orderItem : orderItems) {
        ItemRead itemRead = itemMap.get(orderItem.getItemId());

        if (itemRead != null) {
          long totalItemAmount = itemRead.getPrice().longValue() * orderItem.getQty().longValue(); // 간단한 총액 계산

          OrderRead.OrderItemRead itemDetail = OrderRead.OrderItemRead.builder()
                  .item(itemRead) // 상품 자체 정보
                  .qty(orderItem.getQty()) // 주문 수량 ✅
                  .totalItemAmount(totalItemAmount) // 주문 아이템별 총액
                  .build();
          orderItemReads.add(itemDetail);
        }
      }

      // 6. 주문 조회 DTO에 최종 OrderItemRead 리스트를 설정
      order.setOrderItems(orderItemReads); // ✅ setItems -> setOrderItems

      return order;
    }

    return null;
  }

  // 주문 내용 저장
  @Override
  @Transactional
  public void order(OrderRequest orderReq, Integer memberId) {
    // 1. 주문에 포함된 모든 상품 ID 목록을 추출
    List<Integer> itemsIds = orderReq.getItems().stream().map(OrderRequest.OrderItemRequest::getItemId).collect(Collectors.toList());

    // 2. 주문 상품의 최종 결제 금액을 계산하기 위해 상품 정보 조회
    List<ItemRead> items = itemService.findAll(itemsIds);
    long amount = 0L;

    // 상품 ID를 key로 ItemRead를 빠르게 찾기 위한 Map 생성 (효율성 향상)
    var itemMap = items.stream()
            .collect(Collectors.toMap(ItemRead::getId, item -> item));

    // 3. 최종 결제 금액 계산 시 수량(qty) 반영
    for (OrderRequest.OrderItemRequest reqItem : orderReq.getItems()) {
      ItemRead item = itemMap.get(reqItem.getItemId());

      if (item != null) {
        // (가격 - 할인 금액) * 수량
        long discountedPrice = item.getPrice().longValue() - item.getPrice().longValue() * item.getDiscountPer() / 100;
        amount += discountedPrice * reqItem.getQty();
      } else {
        // 요청된 상품 ID가 유효하지 않은 경우 처리 (예: 예외 발생)
        throw new IllegalArgumentException("Invalid item ID in order request: " + reqItem.getItemId());
      }
    }

    // 4. 주문 요청에 최종 결제 금액 입력
    orderReq.setAmount(amount);

    // 5. 결제 수단이 카드일 때 카드 번호 암호화
    if ("card".equals(orderReq.getPayment())) {
      orderReq.setCardNumber(EncryptionUtils.encrypt(orderReq.getCardNumber()));
    }

    // 6. 주문 저장
    Order order = orderRepository.save(orderReq.toEntity(memberId));

    // 7. 주문 상품을 저장할 빈 배열 리스트 생성 및 OrderItem 엔티티 생성 시 수량 반영
    List<OrderItem> newOrderItems = new ArrayList<>();

    orderReq.getItems().forEach((reqItem) -> {
      // OrderItem 생성자에 Order ID, Item ID, Qty 전달
      OrderItem newOrderItem = new OrderItem(order.getId(), reqItem.getItemId(), reqItem.getQty()); // ✅ qty 추가
      newOrderItems.add(newOrderItem);
    });

    // 주문 상품 데이터 저장
    orderItemService.saveAll(newOrderItems);

    // 특정회원의 주문한 아이템만 장바구니에서 삭제
    newOrderItems.forEach(orderItem -> cartService.remove(order.getMemberId(), orderItem.getItemId()));
  }
}
