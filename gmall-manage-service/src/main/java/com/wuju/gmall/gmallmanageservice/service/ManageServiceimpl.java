package com.wuju.gmall.gmallmanageservice.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.wuju.gmall.*;
import com.wuju.gmall.BaseSaleAttr;
import com.wuju.gmall.Service.ManageService;
import com.wuju.gmall.gmallmanageservice.mapper.*;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Transactional
public class ManageServiceimpl implements ManageService {
    @Autowired
    private SpuInfoMapper spuInfoMapper;
    @Autowired
    private BaseCataLog1Mapper baseCataLog1Mapper;
    @Autowired
    private BaseCataLog2Mapper baseCataLog2Mapper;
    @Autowired
    private BaseCataLog3Mapper baseCataLog3Mapper;
    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;
    @Autowired
    private BaseSalesAttrMapper baseSalesAttrMapper;
    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;
    @Autowired
    private SpuImageMapper spuImageMapper;
    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;
    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;
    @Override
    public List<BaseCatalog1> getCataLog1() {
        List<BaseCatalog1> baseCatalog1s = baseCataLog1Mapper.selectAll();
        return baseCatalog1s;
    }

    @Override
    public List<BaseCatalog2> getCataLog2(String cataLog1Id) {
        BaseCatalog2 baseCatalog2 = new BaseCatalog2();
        baseCatalog2.setCatalog1Id(cataLog1Id);
        List<BaseCatalog2> select = baseCataLog2Mapper.select(baseCatalog2);
        return select;
    }

    @Override
    public List<BaseCatalog3> getCataLog3(String cataLog2Id) {
        BaseCatalog3 baseCatalog3 = new BaseCatalog3();
        baseCatalog3.setCatalog2Id(cataLog2Id);
        List<BaseCatalog3> select = baseCataLog3Mapper.select(baseCatalog3);
        return select;
    }

    @Override
    public List<BaseAttrInfo> getAttrInfo(String cataLog3Id) {
        BaseAttrInfo baseAttrInfo = new BaseAttrInfo();
        baseAttrInfo.setCatalog3Id(cataLog3Id);
        List<BaseAttrInfo> select = baseAttrInfoMapper.select(baseAttrInfo);
        return select;
    }

    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        //判断有无id,有修改，无添加
        if (baseAttrInfo.getId()!=null&&baseAttrInfo.getId().length()>0){
            baseAttrInfoMapper.updateByPrimaryKeySelective(baseAttrInfo);
        }else {
            baseAttrInfoMapper.insertSelective(baseAttrInfo);
        }

        //把原属性全部清空
        BaseAttrValue baseAttrValue = new BaseAttrValue();
        baseAttrValue.setAttrId(baseAttrInfo.getId());
        baseAttrValueMapper.deleteByPrimaryKey(baseAttrValue);
        //添加子属性值
        if (baseAttrInfo.getAttrValueList()!=null&&baseAttrInfo.getAttrValueList().size()>0){
            for (BaseAttrValue attrValue : baseAttrInfo.getAttrValueList()) {
                attrValue.setId(null);
                attrValue.setAttrId(baseAttrInfo.getId());
                baseAttrValueMapper.insertSelective(attrValue);
            }
        }

    }

    @Override
    public BaseAttrInfo getAttrValueList(String attrId) {
        //获取父类属性
        BaseAttrInfo baseAttrInfo = baseAttrInfoMapper.selectByPrimaryKey(attrId);
        BaseAttrValue baseAttrValue = new BaseAttrValue();
        //设置父类id以便查询
        baseAttrValue.setAttrId(baseAttrInfo.getId());

        //查询属性值集合
        List<BaseAttrValue> select = baseAttrValueMapper.select(baseAttrValue);
        //将属性值集合放进父类
        baseAttrInfo.setAttrValueList(select);
        return baseAttrInfo;

    }

    @Override
    public List<SpuInfo> getSpuInfoList(SpuInfo spuInfo) {
        List<SpuInfo> select = spuInfoMapper.select(spuInfo);
        return select;
    }

    @Override
    public List<BaseSaleAttr> getBaseSalesAttrList() {
        List<BaseSaleAttr> baseSalesAttrs = baseSalesAttrMapper.selectAll();
        return baseSalesAttrs;
    }

    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {
        //spuInfo 表示前台传过来的数据
    spuInfoMapper.insertSelective(spuInfo);
    //image
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        if (spuImageList!=null&&spuImageList.size()>0){
            for (SpuImage spuImage : spuImageList) {
                spuImage.setSpuId(spuInfo.getId());
                spuImageMapper.insertSelective(spuImage);
            }
        }

        //保存spuSaleAttr
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        if (spuSaleAttrList!=null&&spuSaleAttrList.size()>0){
            for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
//                spuSaleAttr.setSpuId(spuInfo.getId());
                spuSaleAttrMapper.insertSelective(spuSaleAttr);
                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
                if (spuSaleAttrValueList!=null&&spuSaleAttrValueList.size()>0){
                    for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                        spuSaleAttrValue.setSpuId(spuInfo.getId());
                        spuSaleAttrValueMapper.insertSelective(spuSaleAttrValue);
                    }
                }
            }
        }

    }
    public <T> boolean checkListEmpty(List<T> list){
        if (list!=null&&list.size()>0){
            return true;
        }else {
            return false;
        }
    }
}
