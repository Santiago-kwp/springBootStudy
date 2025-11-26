package com.ssg.myGallery.item.controller;

import com.ssg.myGallery.item.dto.ItemRead;
import com.ssg.myGallery.item.service.ItemService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class ItemController {

  private final ItemService itemService;

  @GetMapping("/api/items")
  public ResponseEntity<?> readAll(){
    List<ItemRead> items = itemService.findAll();
    return new ResponseEntity<>(items, HttpStatus.OK);
  }



}
