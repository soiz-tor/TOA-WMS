package com.wms.goodsservice.controller;

import com.wms.api.entity.Goods;
import com.wms.api.service.GoodsService;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class GoodsServiceController {

    @Resource
    GoodsService goodsService;

    @GetMapping(value = "/goods/all", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> listGoodsController(@RequestParam("page") int page, @RequestParam("size") int size) {
        return goodsService.listGoodsService(page, size);
    }

    @GetMapping(value = "/goods/search", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> searchGoodsController(@RequestParam("t") String search) {
        return goodsService.searchGoodsService(search);
    }

    @GetMapping(value = "/goods/detail/{goodsNo}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> detailGoodsController(@PathVariable("goodsNo") String goodsNo) {
        return goodsService.detailGoodsService(goodsNo);
    }

    @PostMapping(value = "/goods", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> addGoodsController(@RequestBody Goods goods) {
        return goodsService.addGoodsService(goods);
    }

    @PutMapping(value = "/goods", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateGoodsController(@RequestBody Goods goods) {
        return goodsService.updateGoodsService(goods);
    }

    @DeleteMapping(value = "/goods", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> deleteGoodsController(@RequestBody Map<String, List<String>> goodsNoList) {
        return goodsService.deleteGoodsService(goodsNoList);
    }
}
