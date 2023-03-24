package com.wms.goodsservice.mapper;

import com.wms.api.entity.Goods;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface GoodsMapper {

    @Select("select * from goods limit #{page}, #{size}")
    List<Goods> listGoods(@Param("page") int page, @Param("size") int size);

    @Select("select * from goods where goodsNo like concat('%',#{search}) or name like concat('%',#{search}) or janNo like concat('%',#{search})")
    List<Goods> searchGoods(@Param("search") String search);

    @Select("select * from goods where goodsNo = #{goodsNo}")
    Goods detailGoods(@Param("goodsNo") String goodsNo);

    @Insert("insert into goods(goodsNo, img_url, name, janNo, type, unit, price, create_time, update_time) values(#{goodsNo}, #{imgUrl}, #{name}, #{janNo}, #{type}, #{unit}, #{price}, #{createTime}, #{updateTime})")
    void addGoods(@Param("goodsNo") String goodsNo, @Param("imgUrl") String imgUrl, @Param("name") String name, @Param("janNo") String janNo, @Param("type") String type, @Param("unit") String unit, @Param("price") BigDecimal price, @Param("createTime") Timestamp createTime, @Param("updateTime") Timestamp updateTime);

    @Update("update goods set img_url = #{imgUrl}, name = #{name}, janNo = #{janNo}, type = #{type}, unit = #{unit}, price = #{price}, update_time = #{updateTime} where goodsNo = #{goodsNo}")
    void updateGoods(@Param("goodsNo") String goodsNo, @Param("imgUrl") String imgUrl, @Param("name") String name, @Param("janNo") String janNo, @Param("type") String type, @Param("unit") String unit, @Param("price") BigDecimal price, @Param("updateTime") Timestamp updateTime);

    @Delete("<script>"+"delete from goods where goodsNo in ("+"<foreach collection='goodsNoList' item='goodsNo' separator=','>" +
            "#{goodsNo} "+
            "</foreach>" +")" + "</script>")
    void deleteGoods(@Param("goodsNoList") List<String> goodsNoList);
}
