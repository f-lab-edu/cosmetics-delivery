package com.cosmetics.controller;

import com.cosmetics.api.controller.GoodsController;
import com.cosmetics.domain.exception.custom.CustomException;
import com.cosmetics.domain.exception.error.GoodsErrorManagement;
import com.cosmetics.domain.goods.dto.GoodsManagementRequest;
import com.cosmetics.domain.goods.dto.item.GoodsItemManagementRequest;
import com.cosmetics.domain.goods.service.GoodsService;
import com.cosmetics.domain.sms.service.SmsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 컨트롤러 단위 테스트
 * 들어가면 안되는 필드가 들어갔는지는 어떻게 테스트 할 수 있을까요?
 */
@WebMvcTest(GoodsController.class)
@Slf4j
public class GoodsControllerTest2 {

    @MockBean
    private GoodsService goodsService;
    @MockBean
    private SmsService smsService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("상품등록 파라미터가 누락되었을 경우_validation handler_MethodArgumentNotValidException 테스트")
    public void validGoodsTest() throws Exception {
        //given
        GoodsManagementRequest goodsManagement = requestValidGoods();

        //when
        ResultActions resultActions = mockMvc.perform(
                post("http://localhost:8080/v1/goods")
                        .contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(goodsManagement)));

        resultActions.andExpect(status().isBadRequest());
        resultActions.andDo(print());
        //then
        resultActions.andExpect(jsonPath("errorCode").value("INVALID_PARAMETER"));
        resultActions.andExpect(jsonPath("errorMessage").value("유효하지 않는 값입니다"));
        resultActions.andExpect(jsonPath("$.fieldErrorList[0].field").value(anyOf(is("items[0].itemNm"), is("goodsNm"))));
        resultActions.andExpect(jsonPath("$.fieldErrorList[0].message").value("must not be blank"));
        resultActions.andExpect(jsonPath("$.fieldErrorList[1].field").value(anyOf(is("items[0].itemNm"), is("goodsNm"))));
        resultActions.andExpect(jsonPath("$.fieldErrorList[1].message").value("must not be blank"));
    }

    @Test
    @DisplayName("상품등록 시 내부오류로 상품등록이 실패했을 경우_커스텀 예외_CustomExceptionHandler 테스트")
    public void saveGoodsFailErrorTest() throws Exception {
        GoodsManagementRequest goodsManagement = requestGoods();

        given(goodsService.save(any())).willThrow(new CustomException(GoodsErrorManagement.GOODS_SAVE_ERROR));

        //when
        ResultActions resultActions = mockMvc.perform(
                post("http://localhost:8080/v1/goods")
                        .contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(goodsManagement)));
        resultActions.andDo(print()).andExpect(jsonPath("errorCode").value("GOODS_SAVE_ERROR"));
        resultActions.andExpect(jsonPath("errorMessage").value("상품 등록에 실패하였습니다  잠시 후에 시도해 주세요"));

    }

    @Test
    @DisplayName("삭제할 상품번호가 존재하지 않습니다_IllegalArgumentExceptionHandler 테스트 ")
    public void illegalGoodsTest() throws Exception {
        Long goodsNo = 1L;
        given(goodsService.deleteByGoodsNo(goodsNo)).willThrow(new IllegalArgumentException("존재하지 않는 상품입니다"));

        mockMvc.perform(MockMvcRequestBuilders.delete("http://localhost:8080/v1/goods/{goodsNo}", goodsNo))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("INVALID_PARAMETER"))
                .andExpect(jsonPath("errorMessage").value("존재하지 않는 상품입니다"));
    }

    private static GoodsManagementRequest requestGoods() {
        //item
        List<GoodsItemManagementRequest> items = new ArrayList<>();

        items.add(GoodsItemManagementRequest.builder()
                .itemNm("건성용")
                .itemQty(50).build());
        items.add(GoodsItemManagementRequest.builder()
                .itemNm("지성용")
                .itemQty(30).build());

        return GoodsManagementRequest.builder()
                .category("스킨케어")
                .goodsNm("닥터스킨")
                .marketPrice(15000)
                .salePrice(12000)
                .supplyPrice(10000)
                .vendorId(1L)
                .stockQty(80)
                .brandNm("닥터펫")
                .saleStartDtime("2024-05-01 00:00:00")
                .saleEndDtime("2024-08-01 00:00:00")
                .image("https://cdn.localhost:8081/images/lv202400002/goods/image_1.png")
                .addImage("https://cdn.localhost:8081/images/lv202400002/goods/image_2.png")
                .items(items)
                .build();
    }

    private static GoodsManagementRequest requestValidGoods() {
        //item
        List<GoodsItemManagementRequest> items = new ArrayList<>();

        items.add(GoodsItemManagementRequest.builder()
                //.itemNm("건성용")
                .itemQty(50).build());
        items.add(GoodsItemManagementRequest.builder()
                .itemNm("지성용")
                .itemQty(30).build());

        return GoodsManagementRequest.builder()
                .category("스킨케어")
                //.goodsNm("닥터스킨")
                .marketPrice(15000)
                .salePrice(12000)
                .supplyPrice(10000)
                .vendorId(1L)
                .stockQty(80)
                .brandNm("닥터펫")
                .saleStartDtime("2024-05-01 00:00:00")
                .saleEndDtime("2024-08-01 00:00:00")
                .image("https://cdn.localhost:8081/images/lv202400002/goods/image_1.png")
                .addImage("https://cdn.localhost:8081/images/lv202400002/goods/image_2.png")
                .items(items)
                .build();
    }

}
