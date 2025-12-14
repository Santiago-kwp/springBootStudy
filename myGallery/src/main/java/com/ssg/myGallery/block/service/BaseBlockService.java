package com.ssg.myGallery.block.service;

import com.ssg.myGallery.block.entity.Block;
import com.ssg.myGallery.block.repository.BlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.digest.DigestUtils; // SHA-256 해시 유틸

@Service
@RequiredArgsConstructor
public class BaseBlockService implements BlockService {

  private final BlockRepository blockRepository;

  @Override
  public void add(String token) {
    String hash = DigestUtils.sha256Hex(token);
    if (!blockRepository.existsByTokenHash(hash)) {
      blockRepository.save(new Block(hash));
    }
  }

  @Override
  public boolean has(String token) {
    String hash = DigestUtils.sha256Hex(token);
    return blockRepository.existsByTokenHash(hash);
  }
}