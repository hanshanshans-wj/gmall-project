package com.wuju.gmall.gmallmanageweb.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.wuju.gmall.*;
import com.wuju.gmall.Service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
public class ManageController {
    @Reference
    private ManageService manageService;
    @RequestMapping("getCatalog1")
    public List<BaseCatalog1> getCataLog1(){
        List<BaseCatalog1> cataLog1 = manageService.getCataLog1();
        return cataLog1;
    }
    @RequestMapping("getCatalog2")
    public List<BaseCatalog2> getCatalog2(String catalog1Id){
        List<BaseCatalog2> cataLog2 = manageService.getCataLog2(catalog1Id);
        return cataLog2;
    }
    @RequestMapping("getCatalog3")
    public List<BaseCatalog3> getCatalog3(String catalog2Id){
        List<BaseCatalog3> cataLog3 = manageService.getCataLog3(catalog2Id);
        return cataLog3;
    }
    @RequestMapping("attrInfoList")
    public List<BaseAttrInfo> attrInfoList(String catalog3Id){
        List<BaseAttrInfo> attrInfo = manageService.getAttrInfo(catalog3Id);
        return attrInfo;
    }
    @RequestMapping("saveAttrInfo")
    public void saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        manageService.saveAttrInfo(baseAttrInfo);
    }
    @RequestMapping("getAttrValueList")
    public List<BaseAttrValue> getAttrValueList(String attrId){
        BaseAttrInfo baseAttrInfo=manageService.getAttrValueList(attrId);
        return baseAttrInfo.getAttrValueList();
    }



}
