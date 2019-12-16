package com.wuju.gmall.gmallmanageservice.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.wuju.gmall.*;
import com.wuju.gmall.Service.ManageService;
import com.wuju.gmall.config.RedisUtil;
import com.wuju.gmall.gmallmanageservice.constant.ManageConst;
import com.wuju.gmall.gmallmanageservice.mapper.*;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.concurrent.TimeUnit;

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
    @Autowired
    private SkuInfoMapper skuInfoMapper;
    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;
    @Autowired
    private SkuImageMapper skuImageMapper;
    @Autowired
    private SkuSaleValueMapper skuSaleValueMapper;
    @Autowired
    private RedisUtil redisUtil;
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
                spuSaleAttr.setSpuId(spuInfo.getId());
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

    @Override
    public List<SpuImage> getSpuImageList(String spuId) {
        SpuImage spuImage = new SpuImage();
        spuImage.setSpuId(spuId);
        List<SpuImage> select = spuImageMapper.select(spuImage);
        return select;
    }

    @Override
    public List<BaseAttrInfo> selectBaseAttrInfoListByCatalog3Id(String catalog3Id) {
        List<BaseAttrInfo> baseAttrInfos = baseAttrInfoMapper.selectBaseAttrInfoListByCatalog3Id(catalog3Id);

        return baseAttrInfos;
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(String spuId) {
        List<SpuSaleAttr> spuSaleAttrList = spuSaleAttrMapper.getSpuSaleAttrList(spuId);

        return spuSaleAttrList;
    }

    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {

        if (skuInfo.getId()==null||skuInfo.getId().length()==0){
//            skuInfo.setId(null);
            skuInfoMapper.insertSelective(skuInfo);
        }else{
            skuInfoMapper.updateByPrimaryKeySelective(skuInfo);
        }
//        SkuAttrValue skuAttrValue1 = new SkuAttrValue();
//        skuAttrValue1.setSkuId(skuInfo.getId());
//        skuAttrValueMapper.delete(skuAttrValue1);
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        if (checkListEmpty(skuAttrValueList)){
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                skuAttrValue.setSkuId(skuInfo.getId());
                skuAttrValueMapper.insertSelective(skuAttrValue);
            }
        }
//        SkuImage skuImage1 = new SkuImage();
//        skuImage1.setSkuId(skuInfo.getId());
//        skuImageMapper.delete(skuImage1);
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        if (checkListEmpty(skuImageList)){
            for (SkuImage skuImage : skuImageList) {
                skuImage.setSkuId(skuInfo.getId());
                skuImageMapper.insertSelective(skuImage);
            }
        }
//        SkuSaleAttrValue skuSaleAttrValue1 = new SkuSaleAttrValue();
//        skuSaleAttrValue1.setSkuId(skuInfo.getId());
//        skuSaleValueMapper.delete(skuSaleAttrValue1);
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        if (checkListEmpty(skuSaleAttrValueList)){
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
                skuSaleAttrValue.setSkuId(skuInfo.getId());
                skuSaleValueMapper.insertSelective(skuSaleAttrValue);
            }
        }
    }

    @Override
    public SkuInfo getSkuInfo(String skuId) {
        return getSkuInfoSet(skuId);
    }

    private SkuInfo getSkuInfoRedisson(String skuId) {
//        try{
//            Jedis jedis = redisUtil.getJedis();
//            jedis.set("k1","v1");
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        SkuInfo skuInfo = new SkuInfo();
        Jedis jedis=null;
        //获取jedis
        try {
            jedis = redisUtil.getJedis();
            //定义key
            String skuInfoKey= ManageConst.SKUKEY_PREFIX+skuId+ManageConst.SKUKEY_SUFFIX;
            //获取缓存数据
            String skuJson = jedis.get(skuInfoKey);
            //什么时候加锁
            if (skuJson==null){
                Config config = new Config();
                config.useSingleServer().setAddress("redis://192.168.25.130:6379");
                //创建redisson
                RedissonClient redisson = Redisson.create(config);
                RLock lock = redisson.getLock("my-lock");
//                boolean locked = lock.isLocked();
//                Lock.lock(10,TimeUnit.SECONDS);
                boolean b = lock.tryLock(100L, 10L, TimeUnit.SECONDS);
                if (b){
                    try {
                        skuInfo=getSkuInfoDB(skuId);
                        jedis.setex(skuInfoKey,ManageConst.SKUKEY_TIMEOUT, JSON.toJSONString(skuInfo));
                    } finally {
                        lock.unlock();
                    }
                }

            }else {
                //有缓存，直接取
                skuInfo = JSON.parseObject(skuJson,SkuInfo.class);
                return skuInfo;

            }
        } catch (Exception e){
            e.printStackTrace();
        } finally{
            //排除空指针
            if (jedis!=null){
                jedis.close();
            }
        }
        return getSkuInfoDB(skuId);
    }

    private SkuInfo getSkuInfoSet(String skuId) {
        SkuInfo skuInfo = new SkuInfo();
        Jedis jedis = null;
        try {
            jedis = redisUtil.getJedis();

            String skuInfoKey= ManageConst.SKUKEY_PREFIX+skuId+ManageConst.SKUKEY_SUFFIX;
            String skuJson =jedis.get(skuInfoKey);
            if (skuJson==null){
                //存在redis缓存中，取出
                // 没有数据 ,需要加锁！取出完数据，还要放入缓存中，下次直接从缓存中取得即可！
                System.out.println("没有命中缓存");
                // 定义key user:userId:lock
                String skuLockKey=ManageConst.SKUKEY_PREFIX+skuId+ManageConst.SKULOCK_SUFFIX;
                //锁的值
                String token = UUID.randomUUID().toString().replace("-", "");

                //生成锁
                String lockKey = jedis.set(skuLockKey, token, "NX", "PX", ManageConst.SKULOCK_EXPIRE_PX);
                if ("OK".equals(lockKey)){
                    //加锁成功，调用数据库查询
                    System.out.println("获取分布式锁成功");
                    //查询数据库放入缓存
                    skuInfo=getSkuInfoDB(skuId);
                    //设置过期时间
                    jedis.setex(skuInfoKey,ManageConst.SKUKEY_TIMEOUT, JSON.toJSONString(skuInfo));
                    // 解锁  jedis.del(lockKey); 误删锁！ lua 脚本。。。
                    String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                    // 执行删除锁！
                    jedis.eval(script, Collections.singletonList(skuLockKey), Collections.singletonList(token));
                    return skuInfo;
                }else {
                    //进入等待
                    Thread.sleep(1000);
                    return getSkuInfo(skuId);
                }
            }else {
                skuInfo = JSON.parseObject(skuJson,SkuInfo.class);
                return skuInfo;

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis!=null){
                jedis.close();
            }
        }

        return getSkuInfoDB(skuId);
    }

    private SkuInfo getSkuInfoDB(String skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectByPrimaryKey(skuId);
        SkuImage skuImage = new SkuImage();
        skuImage.setSkuId(skuId);
        List<SkuImage> select = skuImageMapper.select(skuImage);
        skuInfo.setSkuImageList(select);
        SkuAttrValue skuAttrValue = new SkuAttrValue();
        skuAttrValue.setSkuId(skuId);
        List<SkuAttrValue> select1 = skuAttrValueMapper.select(skuAttrValue);
        skuInfo.setSkuAttrValueList(select1);
        return skuInfo;
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(SkuInfo skuInfo) {
        List<SpuSaleAttr> s=spuSaleAttrMapper.selectSpuSaleAttrListCheckBySku(skuInfo.getId(),skuInfo.getSpuId());
        return s;
    }

    @Override
    public List<SkuSaleAttrValue> getSkuSaleAttrValueListBySpu(String spuId) {
        List<SkuSaleAttrValue> skuSaleAttrValueList=skuSaleValueMapper.selectSkuSaleAttrValueListBySpu(spuId);
        return skuSaleAttrValueList;
    }

    @Override
    public Map getSkuValueIdsMap(String spuId) {
        List<Map> mapList=skuSaleValueMapper.getSaleAttrValuesBySpu(spuId);
        HashMap<Object, Object> map = new HashMap<>();
        for (Map map1 : mapList) {
            map.put(map1.get("value_ids"),map.get("sku_id"));
        }
        return map;
    }

    @Override
    public List<BaseAttrInfo> getAttrList(List<String> attrValueIdList) {
        String attrValueIds = StringUtils.join(attrValueIdList, ",");
        List<BaseAttrInfo> baseAttrList=baseAttrInfoMapper.selectAttrInfoListByIds(attrValueIds);
        return baseAttrList;
    }

    public <T> boolean checkListEmpty(List<T> list){
        if (list!=null&&list.size()>0){
            return true;
        }else {
            return false;
        }
    }
}
