package com.mcptest.mcptoy.item.repository;

import com.mcptest.mcptoy.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {

  // 여러 상품 아이디로 상품 데이터를 조회하는 메소드
  List<Item> findAllByIdIn(List<Integer> ids);

}

