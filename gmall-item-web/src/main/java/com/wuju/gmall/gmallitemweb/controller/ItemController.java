package com.wuju.gmall.gmallitemweb.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.wuju.gmall.Service.ManageService;
import com.wuju.gmall.SkuInfo;
import com.wuju.gmall.SkuSaleAttrValue;
import com.wuju.gmall.SpuSaleAttr;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@Controller
public class ItemController {
    @Reference
    private ManageService manageService;
    @RequestMapping("{skuId}.html")
    public String getSkuInfo(@PathVariable(value = "skuId") String skuId, HttpServletRequest request){
        SkuInfo skuInfo = manageService.getSkuInfo(skuId);
        request.setAttribute("skuInfo",skuInfo);
        List<SpuSaleAttr> saleAttrList=manageService.getSpuSaleAttrListCheckBySku(skuInfo);
        request.setAttribute("saleAttrList",saleAttrList);
        //通过spuid来进行不同的sku切换
        String key="";
        HashMap<String,String> map=new HashMap<>();
        List<SkuSaleAttrValue> skuSaleAttrValueList=manageService.getSkuSaleAttrValueListBySpu(skuInfo.getSpuId());
        // {"122|126":"37","123|126":"38","124|128":"39","122|128":"40"} json 字符串！
        // 声明一个map  | map.put("122|126","37"); --- map 转换为json 字符串！ key = valueId|valueId  value = skuId
        if (skuSaleAttrValueList!=null&&skuSaleAttrValueList.size()>0){

                // 拼接规则：1.  当循环的skuId 与 下一次循环的skuId 不相同的时候，停止拼接，并将key，value 放入map 集合中！ 当前key 应该清空！
                //           2.  循环到集合最后的时候，停止拼接 并将key，value 放入map 集合中！ 当前key 应该清空！
                //              map.put("122|126","37")
                //  itar
            for (int i = 0; i <skuSaleAttrValueList.size() ; i++) {
                SkuSaleAttrValue skuSaleAttrValue = skuSaleAttrValueList.get(i);
                if (key.length()>0){
                    key+="|";
                }
                key+=skuSaleAttrValue.getSaleAttrValueId();
                if ((i+1)==skuSaleAttrValueList.size()||!skuSaleAttrValue.getSkuId().equals(skuSaleAttrValueList.get(i+1).getSkuId())){
                    //停止拼接
                    map.put(key,skuSaleAttrValue.getSkuId());
                    key="";
                }
            }

        }
        //转化成json字符串
        String valuesSkuJson = JSON.toJSONString(map);
        //保存数据，渲染
        request.setAttribute("valuesSkuJson",valuesSkuJson);
        return "item";
    }
}
