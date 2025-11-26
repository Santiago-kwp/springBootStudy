package com.ssg.myGallery.cart.controller;

import com.ssg.myGallery.account.helper.AccountHelper;
import com.ssg.myGallery.cart.dto.CartRead;
import com.ssg.myGallery.cart.dto.CartRequest;
import com.ssg.myGallery.cart.service.CartService;
import com.ssg.myGallery.item.dto.ItemRead;
import com.ssg.myGallery.item.service.ItemService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class CartController {

  private final CartService cartService;
  private final ItemService itemService;
  private final AccountHelper accountHelper;

  @GetMapping("/api/cart/items")
  public ResponseEntity<?> readAll(HttpServletRequest request) {
    // 로그인 회원 아이디
    Integer memberId = accountHelper.getMemberId(request);

    // 장바구니 목록 조회
    List<CartRead> carts = cartService.findAll(memberId);

    // 장바구니 안에 있는 상품 아이디로 상품을 조회
    List<Integer> itemIds = carts.stream().map(CartRead::getItemId).toList();
    List<ItemRead> items = itemService.findAll(itemIds);

    return new ResponseEntity<>(items, HttpStatus.OK);
  }

  @PostMapping("/api/carts")
  public ResponseEntity<?> push(HttpServletRequest request, @RequestBody CartRequest cartReq) {
    // 로그인 회원 아이디
    Integer memberId = accountHelper.getMemberId(request);

    // 장바구니 데이터 조회(특정 상품)
    CartRead cart = cartService.find(memberId, cartReq.getItemId());

    // 장바구니 데이터가 없다면
    if (cart == null) {
      cartService.save(cartReq.toEntity(memberId));
    }

    return new ResponseEntity<>(HttpStatus.OK);
  }

  // 회원의 카트에 상품 하나 삭제
  @DeleteMapping("/api/cart/items/{itemId}")
  public ResponseEntity<?> remove(HttpServletRequest req, @PathVariable("itemId") Integer itemId) { // ⑨
    // 로그인 회원 아이디
    Integer memberId = accountHelper.getMemberId(req);

    cartService.remove(memberId, itemId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  // 회원의 카트에 담긴 상품 전체 삭제
  @DeleteMapping("/api/cart/items")
  public ResponseEntity<?> removeAll(HttpServletRequest req) {
    // 로그인 회원 아이디
    Integer memberId = accountHelper.getMemberId(req);

    cartService.removeAll(memberId);
    return new ResponseEntity<>(HttpStatus.OK);
  }







}

