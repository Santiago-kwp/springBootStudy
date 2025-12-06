package com.mcptest.mcptoy.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// ItemDetail.java (상세 보기용 DTO)
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDetail {
  private int id;
  private String name;
  private String imgPath;
  private Integer price;
  private Integer discountPer;
  private String summary;
  private String description;
}
