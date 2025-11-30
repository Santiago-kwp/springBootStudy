package com.ssg.myGallery.item.service;

import com.ssg.myGallery.item.dto.ItemDetail;
import com.ssg.myGallery.item.dto.ItemRead;
import com.ssg.myGallery.item.entity.Item;
import com.ssg.myGallery.item.repository.ItemRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BaseItemService implements ItemService{

  private final ItemRepository itemRepository;


  @Override
  public Page<ItemRead> findAll(Pageable pageable) {
    Page<Item> itemPage = itemRepository.findAll(pageable);
    return itemPage.map(Item::toRead);
  }

  @Override
  public List<ItemRead> findAll(List<Integer> ids) {
    return itemRepository.findAllById(ids).stream().map(Item::toRead).toList();
  }

  @Override
  public ItemDetail findById(Integer id) {
    Optional<ItemDetail> itemDetail = itemRepository.findById(id).map(Item::toDetail);
    return itemDetail.orElse(null);
  }

  @Override
  public List<ItemRead> findItemsByIds(List<Integer> itemIds) {
    return itemRepository.findAllById(itemIds).stream().map(Item::toRead).toList();
  }
}