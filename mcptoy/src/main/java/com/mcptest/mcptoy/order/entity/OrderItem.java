package com.mcptest.mcptoy.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "order_items")
public class OrderItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false)
  private Integer orderId;

  @Column(nullable = false)
  private Integer itemId;

  @Column(nullable = false)
  private Integer qty; // 추가

  @Column(nullable = false, updatable = false)
  @CreationTimestamp
  private LocalDateTime created;

  public OrderItem() {
  }

  public OrderItem(Integer orderId, Integer itemId, Integer qty) { // ⑧
    this.orderId = orderId;
    this.itemId = itemId;
    this.qty = qty;
  }
}