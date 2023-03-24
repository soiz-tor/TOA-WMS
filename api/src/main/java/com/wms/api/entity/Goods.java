package com.wms.api.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public record Goods(String goodsNo, String imgUrl, String name, String janNo, String type, String unit, BigDecimal price, Timestamp createTime, Timestamp updateTime) {
}
