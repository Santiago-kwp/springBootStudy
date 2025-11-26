package com.ssg.myGallery.cart.service;

import com.ssg.myGallery.cart.dto.CartRead;
import com.ssg.myGallery.cart.entity.Cart;
import com.ssg.myGallery.cart.repository.CartRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BaseCartService implements CartService{

  private final CartRepository cartRepository;

  @Override
  public List<CartRead> findAll(Integer memberId) {
    return cartRepository.findAllByMemberId(memberId).stream().map(Cart::toRead).toList();
  }

  @Override
  public CartRead find(Integer memberId, Integer itemId) {
    Optional<Cart> cartOptional = cartRepository.findByMemberIdAndItemId(memberId, itemId);

    return cartOptional.map(Cart::toRead).orElse(null);
  }

  // 특정 회원의 장바구니 상품 전체 삭제
  @Override
  @Transactional
  public void removeAll(Integer memberId) {

    cartRepository.deleteByMemberId(memberId);
  }

  // 특정 회원의 장바구니 특정 상품 데이터 삭제
  @Override
  @Transactional
  public void remove(Integer memberId, Integer itemId) {
    cartRepository.deleteByMemberIdAndItemId(memberId, itemId);
  }

  @Override
  public void save(Cart cart) {
    cartRepository.save(cart);
  }
}
