package com.cosmetics.api.controller;

import com.cosmetics.domain.goods.dto.GoodsManagement;
import com.cosmetics.domain.goods.dto.GoodsManagementRequest;
import com.cosmetics.domain.goods.dto.GoodsManagementResponse;
import com.cosmetics.domain.goods.service.GoodsService;
import com.cosmetics.domain.sms.service.SmsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping(value = "/v1/goods")
@RequiredArgsConstructor
@Slf4j
public class GoodsController {

    private final GoodsService goodsService;
    private final SmsService smsService;

    @GetMapping(value = "/{goodsNo}")
    public CompletableFuture<GoodsManagementResponse> findGoods(@PathVariable Long goodsNo) {

        CompletableFuture<GoodsManagementResponse> goodsResponseFuture = CompletableFuture.supplyAsync(() -> {
            log.error("findGoodsThread = {}", Thread.currentThread().getName());
            GoodsManagement goodsManagement = goodsService.findByGoodsNo(goodsNo);
            return GoodsManagementResponse.toResponseDto(goodsManagement);

        }).thenApplyAsync( //callback
                (GoodsManagementResponse response) -> {
                    log.error("findGoods thenApplayAsync= {}", Thread.currentThread().getName());
                    response.updateSuccess("0000", "조회성공");
                    return response;
                }
        );

        return goodsResponseFuture;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompletableFuture<GoodsManagementResponse> registerGoods(@RequestBody @Valid GoodsManagementRequest goodsManagementRequest) throws ExecutionException, InterruptedException {

        CompletableFuture<GoodsManagementResponse> goodsResponseFuture = CompletableFuture.supplyAsync(() -> {
            log.error("goodsName = {}, registerGoodsThread = {}", goodsManagementRequest.getGoodsNm(), Thread.currentThread().getName()); //ForkJoinPool.commonPool-worker-1
            return goodsService.save(goodsManagementRequest.toServiceDto()); //등록

        }).thenApplyAsync(
                //dto ->
                (goodsManagement) -> {
                    log.error("goodsName = {}, goodsNo = {}", goodsManagementRequest.getGoodsNm(), goodsManagement.getGoodsNo());
                    log.error("registerGoodsThread thenApplyAsync = {}", Thread.currentThread().getName()); //ForkJoinPool.commonPool-worker-1
                    GoodsManagementResponse response = GoodsManagementResponse.toResponseDto(goodsManagement);
                    response.updateSuccess("0000", "등록성공");
                    return response;
                }
        );

        //등록에 성공하면 메세지를 보낸다고 가정
        if ("0000".equals(goodsResponseFuture.get().getResultCode())) {
            smsService.smsMessage(goodsResponseFuture.get().getGoodsNo()); //Cosmetics-Thread-Pool1
        }
        return goodsResponseFuture;
    }

    @DeleteMapping(value = "/{goodsNo}")
    public GoodsManagementResponse deleteGoods(@PathVariable Long goodsNo) {
        log.error("deleteGoods : {}", goodsNo);
        GoodsManagement goodsManagement = goodsService.deleteByGoodsNo(goodsNo);
        //dto -> responseDto
        GoodsManagementResponse responseGoodsManagement = GoodsManagementResponse.toResponseDto(goodsManagement);
        responseGoodsManagement.updateSuccess("0000", "삭제성공");
        return responseGoodsManagement;
    }

}
