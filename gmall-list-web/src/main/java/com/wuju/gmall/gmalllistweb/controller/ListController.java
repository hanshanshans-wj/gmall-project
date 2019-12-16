package com.wuju.gmall.gmalllistweb.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.wuju.gmall.BaseAttrInfo;
import com.wuju.gmall.BaseAttrValue;
import com.wuju.gmall.Service.ListService;
import com.wuju.gmall.Service.ManageService;
import com.wuju.gmall.SkuLsParams;
import com.wuju.gmall.SkuLsResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Controller
public class ListController {
    @Reference
    private ListService listService;
    @Reference
    private ManageService manageService;

    @RequestMapping("list.html")

    public String getList(SkuLsParams skuLsParams, HttpServletRequest request){
        // 设置每页显示的条数
        skuLsParams.setPageSize(2);
        SkuLsResult skuLsResult = listService.search(skuLsParams);

        List<String> attrValueIdList = skuLsResult.getAttrValueIdList();

        List<BaseAttrInfo> attrList=manageService.getAttrList(attrValueIdList);
        ArrayList<BaseAttrValue> baseAttrValues = new ArrayList<>();
        // 已选的属性值列表\
        String urlParam=makeUrlParam(skuLsParams);

        if (attrList!=null&&attrList.size()>0){
            for (Iterator<BaseAttrInfo> iterator = attrList.iterator(); iterator.hasNext(); ) {
                BaseAttrInfo baseAttrInfo = iterator.next();
                List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
                for (BaseAttrValue baseAttrValue : attrValueList) {
                    if (skuLsParams.getValueId()!=null&&skuLsParams.getValueId().length>0){
                        for (String valueId : skuLsParams.getValueId()) {
                            if (valueId.equals(baseAttrValue.getId())){
                                iterator.remove();
                                BaseAttrValue baseAttrValue1 = new BaseAttrValue();
                                baseAttrValue1.setValueName(baseAttrInfo.getAttrName()+":"+baseAttrValue.getValueName());
                                String newUrlParam = makeUrlParam(skuLsParams, valueId);
                                baseAttrValue1.setUrlParam(newUrlParam);
                                baseAttrValues.add(baseAttrValue1);

                            }
                        }
                    }
                }
            }
        }
        request.setAttribute("baseAttrValues",baseAttrValues);
        request.setAttribute("keyword",skuLsParams.getKeyword());
        request.setAttribute("keyword",skuLsParams.getKeyword());
        request.setAttribute("skuLsInfoList",skuLsResult.getSkuLsInfoList());
        request.setAttribute("attrList",attrList);
        request.setAttribute("urlParam",urlParam);
        request.setAttribute("totalPages",skuLsResult.getTotalPages());
        request.setAttribute("pageNo",skuLsParams.getPageNo());
        return "list";
    }

    private String makeUrlParam(SkuLsParams skuLsParams,String... excludeValueIds) {
        String urlParams="";
        if (skuLsParams.getKeyword()!=null&&skuLsParams.getKeyword().length()>0){
            urlParams+="keyword="+skuLsParams.getKeyword();
        }
        if (skuLsParams.getCatalog3Id()!=null&&skuLsParams.getCatalog3Id().length()>0){
            urlParams+="catalog3Id="+skuLsParams.getCatalog3Id();
        }
        if (skuLsParams.getValueId()!=null&&skuLsParams.getValueId().length>0){
            for (String valueId : skuLsParams.getValueId()) {
                if (excludeValueIds!=null&&excludeValueIds.length>0){
                    String excludeValueId = excludeValueIds[0];
                    //点击的参数看是否相等，决定拼接不拼接，相等不拼接
                    if (excludeValueId.equals(valueId)){
                        continue;
                    }
                }
                if (urlParams.length()>0){
                    urlParams+="&";
                }
                urlParams+="valueId="+valueId;
            }
        }
        return urlParams;
    }
}
