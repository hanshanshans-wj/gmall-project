package com.wuju.gmall.gmallmanageservice.mapper;

import com.wuju.gmall.SkuSaleAttrValue;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface SkuSaleValueMapper extends Mapper<SkuSaleAttrValue> {
    List<SkuSaleAttrValue> selectSkuSaleAttrValueListBySpu(String spuId);

    List<Map> getSaleAttrValuesBySpu(String spuId);
}
