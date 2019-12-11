package com.wuju.gmall;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
@Data
public class SkuAttrValue implements Serializable {
    @Id
    @Column
    private String id;
    @Column
    private String attrId;//平台属性id
    @Column
    private String valueId;//平台属性值id
    @Column
    private String skuId;//
}
