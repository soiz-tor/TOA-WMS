package com.wms.api.service;

import com.wms.api.entity.Goods;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface GoodsService {

    ResponseEntity<Map<String, Object>> listGoodsService(int page, int size);

    ResponseEntity<Map<String, Object>> searchGoodsService(String search);

    ResponseEntity<Map<String, Object>> detailGoodsService(String goodsNo);

    ResponseEntity<Map<String, Object>> addGoodsService(Goods goods);

    ResponseEntity<Map<String, Object>> updateGoodsService(Goods goods);

    ResponseEntity<Map<String, Object>> deleteGoodsService(Map<String, List<String>> goodsNoList);
}
