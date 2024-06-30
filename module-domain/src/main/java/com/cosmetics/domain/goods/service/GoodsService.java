package com.cosmetics.domain.goods.service;

import com.cosmetics.domain.goods.dto.GoodsManagement;
import com.cosmetics.domain.goods.entity.GoodsManagementEntity;
import com.cosmetics.domain.goods.repository.GoodsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoodsService {

    private final GoodsRepository goodsRepository;

    @Transactional
    //@Async //써야하나?
    public GoodsManagement findByGoodsNo(Long goodsNo) {
        log.error("GoodsService Thread = {}", Thread.currentThread().getName());
        //여기도 비동기처리를해야하나?
        GoodsManagementEntity goodsManagementEntity = goodsRepository.findByGoodsNo(goodsNo).orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
        //entity -> dto
        return GoodsManagement.toDto(goodsManagementEntity);
    }

    @Transactional
    public GoodsManagement save(GoodsManagement goodsManagement) {
        //Service단은 따로 작업을 안했는데 이게 맞을까요?
        GoodsManagementEntity resultGoodsManagementEntity = goodsRepository.save(goodsManagement.toEntity());

        //entity -> dto
        return GoodsManagement.toDto(resultGoodsManagementEntity);
    }

    @Transactional
    public GoodsManagement deleteByGoodsNo(Long goodsNo) {
        GoodsManagementEntity goodsManagementEntity = goodsRepository.findByGoodsNo(goodsNo).orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
        long deleteCnt = goodsRepository.deleteByGoodsNo(goodsNo);

        //entity -> dto
        return GoodsManagement.toDto(goodsManagementEntity);
    }
}
