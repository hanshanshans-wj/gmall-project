package com.wuju.gmall.Service;

import com.wuju.gmall.*;

import java.util.List;

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
}
