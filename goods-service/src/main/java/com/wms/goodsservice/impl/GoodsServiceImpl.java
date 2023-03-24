package com.wms.goodsservice.impl;

import com.wms.api.entity.Goods;
import com.wms.api.service.GoodsService;
import com.wms.goodsservice.mapper.GoodsMapper;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GoodsServiceImpl implements GoodsService {

    Logger logger = LoggerFactory.getLogger(GoodsServiceImpl.class);

    @Resource
    private GoodsMapper goodsMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public ResponseEntity<Map<String, Object>> listGoodsService(int page, int size) {
        Map<String, Object> map = new ConcurrentHashMap<>();
        try {
            if (page <= 0 || size <= 0) {
                map.put("code", 1);
                map.put("message", "bad request");
                return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
            }
            map.put("code", 0);
            map.put("data", goodsMapper.listGoods((page-1)*size, size));
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Map<String, Object>> searchGoodsService(String search) {
        Map<String, Object> map = new ConcurrentHashMap<>();
        try {
            if (search.isEmpty()) {
                map.put("code", 1);
                map.put("message", "bad request");
                return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
            }
            map.put("code", 0);
            map.put("data", goodsMapper.searchGoods(search));
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Map<String, Object>> detailGoodsService(String goodsNo) {
        Map<String, Object> map = new ConcurrentHashMap<>();
        try {
            if (goodsNo.isEmpty()) {
                map.put("code", 1);
                map.put("message", "bad request");
                return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
            } else if (goodsMapper.detailGoods(goodsNo) == null) {
                map.put("code", 0);
                map.put("message", "goods is not exist");
                return new ResponseEntity<>(map, HttpStatus.OK);
            }
            map.put("code", 0);
            map.put("data", goodsMapper.detailGoods(goodsNo));
            System.out.println(new Timestamp(System.currentTimeMillis()));
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Map<String, Object>> addGoodsService(Goods goods) {
        Map<String, Object> map = new ConcurrentHashMap<>();
        try {
            if (goods.goodsNo().isEmpty() || goods.imgUrl().isEmpty() || goods.janNo().isEmpty() || goods.type().isEmpty() || goods.name().isEmpty() || goods.unit().isEmpty() || goods.price() == null) {
                map.put("code", 1);
                map.put("message", "bad request");
                return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
            } else if (goodsMapper.detailGoods(goods.goodsNo()) != null) {
                map.put("code", 1);
                map.put("message", "goods is exist");
                return new ResponseEntity<>(map, HttpStatus.OK);
            }
            goodsMapper.addGoods(goods.goodsNo(), goods.imgUrl(), goods.name(), goods.janNo(), goods.type(), goods.unit(), goods.price(), new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
            map.put("code", 0);
            map.put("message", "add goods success");
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Map<String, Object>> updateGoodsService(Goods goods) {
        Map<String, Object> map = new ConcurrentHashMap<>();
        try {
            if (goods.goodsNo().isEmpty() || goods.imgUrl().isEmpty() || goods.janNo().isEmpty() || goods.type().isEmpty() || goods.name().isEmpty() || goods.unit().isEmpty() || goods.price() == null) {
                map.put("code", 1);
                map.put("message", "bad request");
                return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
            } else if (goodsMapper.detailGoods(goods.goodsNo()) == null) {
                map.put("code", 1);
                map.put("message", "goods is not exist");
                return new ResponseEntity<>(map, HttpStatus.OK);
            }
            goodsMapper.updateGoods(goods.goodsNo(), goods.imgUrl(), goods.name(), goods.janNo(), goods.type(), goods.unit(), goods.price(), new Timestamp(System.currentTimeMillis()));
            map.put("code", 0);
            map.put("message", "update goods success");
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Map<String, Object>> deleteGoodsService(Map<String, List<String>> goodsNoList) {
        Map<String, Object> map = new ConcurrentHashMap<>();
        try {
            if (goodsNoList.get("goodsNoList").isEmpty()) {
                map.put("code", 1);
                map.put("message", "bad request");
                return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
            }
            goodsMapper.deleteGoods(goodsNoList.get("goodsNoList"));
            map.put("code", 0);
            map.put("message", "delete goods success");
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
