package com.wuju.gmall.gmallmanageweb.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.wuju.gmall.Service.ManageService;
import com.wuju.gmall.SkuInfo;
import com.wuju.gmall.SpuImage;
import com.wuju.gmall.SpuSaleAttr;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
public class SkuManageController {
    @Reference
    private ManageService manageService;
    @RequestMapping("spuImageList")
    public List<SpuImage> getSpuImageList(String spuId){
        List<SpuImage> list=manageService.getSpuImageList(spuId);
        return list;
    }
    //http://localhost:8082/spuSaleAttrList?spuId=62
    @RequestMapping("spuSaleAttrList")
    public List<SpuSaleAttr> getSpuSaleAttrList(String spuId){
        List<SpuSaleAttr> list=manageService.getSpuSaleAttrList(spuId);
        return list;
    }

    @RequestMapping("saveSkuInfo")
    public void saveSkuInfo(@RequestBody SkuInfo skuInfo){
        manageService.saveSkuInfo(skuInfo);
    }
}
