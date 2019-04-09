package com.pinyougou.sellergoods.service.impl;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.pojogroup.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.content.service.GoodsService;

import entity.PageResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;

	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}


	@Autowired
	private TbItemMapper itemMapper;

	@Autowired
	private TbBrandMapper brandMapper;

	@Autowired
    private TbItemCatMapper itemCatMapper;

	@Autowired
    private TbSellerMapper sellerMapper;

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {

	    goods.getTbGoods().setAuditStatus("0");

	    goods.getTbGoods().setIsMarketable("1");

	    goodsMapper.insert(goods.getTbGoods());

	    goods.getTbGoodsDesc().setGoodsId(goods.getTbGoods().getId());

	    goodsDescMapper.insert(goods.getTbGoodsDesc());


        addItem(goods);


    }

    private void addItem(Goods goods) {
        if("1".equals(goods.getTbGoods().getIsEnableSpec())){
            for(TbItem item :goods.getItemList()){
                //标题
                String title= goods.getTbGoods().getGoodsName();
                Map<String,Object> specMap = JSON.parseObject(item.getSpec());
                for(String key:specMap.keySet()){
                    title+=" "+ specMap.get(key);
                }
                item.setTitle(title);
                setItemValus(goods,item);
                itemMapper.insert(item);
            }
        }else{
            TbItem item=new TbItem();
            item.setTitle(goods.getTbGoods().getGoodsName());//商品KPU+规格描述串作为SKU名称
            item.setPrice( goods.getTbGoods().getPrice() );//价格
            item.setStatus("1");//状态
            item.setIsDefault("1");//是否默认
            item.setNum(99999);//库存数量
            item.setSpec("{}");
            setItemValus(goods,item);
            itemMapper.insert(item);
        }
    }

    private void setItemValus(Goods goods,TbItem item) {
        item.setGoodsId(goods.getTbGoods().getId());//商品SPU编号
        item.setSellerId(goods.getTbGoods().getSellerId());//商家编号
        item.setCategoryid(goods.getTbGoods().getCategory3Id());//商品分类编号（3级）
        item.setCreateTime(new Date());//创建日期
        item.setUpdateTime(new Date());//修改日期

        //品牌名称
        TbBrand brand = brandMapper.selectByPrimaryKey(goods.getTbGoods().getBrandId());
        item.setBrand(brand.getName());
        //分类名称
        TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getTbGoods().getCategory3Id());
        item.setCategory(itemCat.getName());

        //商家名称
        TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getTbGoods().getSellerId());
        item.setSeller(seller.getNickName());

        //图片地址（取spu的第一个图片）
        List<Map> imageList = JSON.parseArray(goods.getTbGoodsDesc().getItemImages(), Map.class) ;
        if(imageList.size()>0){
            item.setImage ( (String)imageList.get(0).get("url"));
        }
    }

	
	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){

	    goodsMapper.updateByPrimaryKey(goods.getTbGoods());

	    goodsDescMapper.updateByPrimaryKey(goods.getTbGoodsDesc());

	    //先删除SKU
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(goods.getTbGoods().getId());

        itemMapper.deleteByExample(example);

        //再保存新的

        addItem(goods);

    }
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		Goods goods = new Goods();

		goods.setTbGoods(goodsMapper.selectByPrimaryKey(id));

		goods.setTbGoodsDesc(goodsDescMapper.selectByPrimaryKey(id));

		TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        List<TbItem> itemList = itemMapper.selectByExample(example);

        goods.setItemList(itemList);
        return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setIsDelete("1");
            goodsMapper.updateByPrimaryKey(tbGoods);
        }
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();

		criteria.andIsDeleteIsNull();
		
		if(goods!=null){			
						if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}


    @Override
    public void updateStatus(Long[] ids, String status) {

        for (Long id: ids) {

            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);

            tbGoods.setAuditStatus(status);

            goodsMapper.updateByPrimaryKey(tbGoods);

        }

    }

    @Override
    public void shangjia(Long[] ids) {
        for (Long id: ids) {
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setIsMarketable("1");
            goodsMapper.updateByPrimaryKey(tbGoods);
        }
    }

    @Override
    public void xiajia(Long[] ids) {
        for (Long id: ids) {
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setIsMarketable("0");
            goodsMapper.updateByPrimaryKey(tbGoods);
        }
    }


    /**
     * 根据SPU 找SKU
     * @param goodsId
     * @return
     */
    @Override
    public List<TbItem> findItemsByGoodsIdAndStatus(Long[] goodsId,String status) {

        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdIn(Arrays.asList(goodsId));
        criteria.andStatusEqualTo(status);

        List<TbItem> itemList = itemMapper.selectByExample(example);

        return itemList;
    }
}
