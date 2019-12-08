package com.wuju.gmall;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

@Data
public class SpuSaleAttr implements Serializable {
    @Column
    @Id
    private String id;
    @Column
    private String spuId;
    @Column
    private String SaleAttrId;
    @Column
    private String SaleAttrName;
    @Transient
    private List<SpuSaleAttrValue> spuSaleAttrValueList;
}
