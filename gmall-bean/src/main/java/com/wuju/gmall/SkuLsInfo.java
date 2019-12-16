package com.wuju.gmall;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuLsInfo implements Serializable {
    String id;

    BigDecimal price;

    String skuName;

    String catalog3Id;

    String skuDefaultImg;
    //热度
    Long hotScore=0L;

    List<SkuLsAttrValue> skuAttrValueList;
}
