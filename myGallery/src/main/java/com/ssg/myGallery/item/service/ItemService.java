package com.ssg.myGallery.item.service;

import com.ssg.myGallery.item.dto.ItemDetail;
import com.ssg.myGallery.item.dto.ItemRead;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ItemService {
  // 전체 상품 목록 조회 = 리턴 타입으로 상품 조회 DTO 리스트
  Page<ItemRead> findAll(Pageable pageable);

  // 상품 목록 조회 (특정 아이디 리스트로 조회) 매개변수로 상품의 아이디 리스트를 받아, 해당 아이디의 상품 정보를 조회
  List<ItemRead> findAll(List<Integer> ids);

  // 상품 하나 상세 조회
  ItemDetail findById(Integer id);


}
