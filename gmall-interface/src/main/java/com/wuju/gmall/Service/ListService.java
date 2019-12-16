package com.wuju.gmall.Service;

import com.wuju.gmall.SkuLsInfo;
import com.wuju.gmall.SkuLsParams;
import com.wuju.gmall.SkuLsResult;

public interface ListService {
    /**
     * 保存skuInfo到es
     * @param skuLsInfo
     */
    public void saveSkuInfo(SkuLsInfo skuLsInfo);

    /**
     * 基于这个DSL查询编写Java代码
     * @param skuLsParams
     * @return
     */
    public SkuLsResult search(SkuLsParams skuLsParams);

    void incrHotScore(String skuId);
}
