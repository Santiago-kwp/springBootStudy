package com.ssg.myGallery.item.controller;

import com.ssg.myGallery.item.dto.ItemDetail;
import com.ssg.myGallery.item.dto.ItemRead;
import com.ssg.myGallery.item.service.ItemService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class ItemController {

  private final ItemService itemService;

  @GetMapping("/api/items")
  public Page<ItemRead> getItems(Pageable pageable) {
    return itemService.findAll(pageable);
  }

  // 상품 상세 조회
  @GetMapping("/api/items/{id}")
  public ResponseEntity<ItemDetail> getItemDetail(@PathVariable int id) {
    
    ItemDetail itemDetail = itemService.findById(id);
    return new ResponseEntity<>(itemDetail, HttpStatus.OK);
  }

  @GetMapping("/api/itemList")
  public ResponseEntity<List<ItemRead>> getItemList(@RequestParam List<Integer> itemIds) {

    List<ItemRead> items = itemService.findItemsByIds(itemIds);

    if (items.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(items);
  }



}
