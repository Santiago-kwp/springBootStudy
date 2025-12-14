package com.ssg.myGallery.block.repository;

import com.ssg.myGallery.block.entity.Block;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockRepository extends JpaRepository<Block, Integer> { // ① 제네릭 매개변수로 토큰 차단 엔티티 Block 와 이 엔티티의 Id 타입인 Integer 지정

  // 존재 여부 확인 최적화
  boolean existsByTokenHash(String tokenHash);
}
