package com.wuju.gmall.Service;

import com.wuju.gmall.*;

import java.util.List;
import java.util.Map;

public interface ManageService {
    /**
     * 一级分类
     * @return
     */
   public  List<BaseCatalog1> getCataLog1();

    /**
     * 二级分类
     * @param cataLog1Id
     * @return
     */
    public  List<BaseCatalog2> getCataLog2(String cataLog1Id);

    /**
     * 三级分类
     * @param cataLog2Id
     * @return
     */
    public  List<BaseCatalog3> getCataLog3(String cataLog2Id);

    /**
     * 三级分类下的详情 平台属性
     * @param cataLog3Id
     * @return
     */
    public  List<BaseAttrInfo> getAttrInfo(String cataLog3Id);

    /**
     * 增加平台属性
     * @param baseAttrInfo
     */
    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    /**
     * 获取修改属性回显
     * @param attrId
     * @return
     */
    BaseAttrInfo getAttrValueList(String attrId);

    /**
     * 查询销售属性列表查询功能
     * @param spuInfo
     * @return
     */
    List<SpuInfo> getSpuInfoList(SpuInfo spuInfo);

 /**
  * 查询基本销售属性表
  * @return
  */
 List<BaseSaleAttr> getBaseSalesAttrList();

 /**
  * 保存商品属性
  * @param spuInfo
  */
 public void saveSpuInfo(SpuInfo spuInfo);

    /**
     * 获取spu图片列表
     * @param spuId
     * @return
     */
    List<SpuImage> getSpuImageList(String spuId);

    /**
     * 根据三级分类id获取平台属性列表
     * @param catalog3Id
     * @return
     */
    List<BaseAttrInfo> selectBaseAttrInfoListByCatalog3Id(String catalog3Id);

    /**
     * 根据、spuid获取销售属性列表
      * @param spuId
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrList(String spuId);

    /**
     * 保存skuInfo属性
     * @param skuInfo
     */
    void saveSkuInfo(SkuInfo skuInfo);

    /**
     * 根据skuId获取sku对象
     * @param skuId
     * @return
     */
    SkuInfo getSkuInfo(String skuId);

    /**
     * 通过skuInfo查询销售属性值集合
     * @param skuInfo
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(SkuInfo skuInfo);

    /**
     * 销售属性值切换
     * @param spuId
     * @return
     */
    List<SkuSaleAttrValue> getSkuSaleAttrValueListBySpu(String spuId);

    Map getSkuValueIdsMap(String spuId);

    List<BaseAttrInfo> getAttrList(List<String> attrValueIdList);
}
