package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.content.service.BrandService;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private TbBrandMapper brandMapper;

    @Override
    public List<TbBrand> findAll() {
        return brandMapper.selectByExample(null);
    }

    /**
     * 分页查询品牌数据
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        //添加分页即可
        PageHelper.startPage(pageNum, pageSize);
        //强转为Page
        Page<TbBrand> tbBrands = (Page<TbBrand>) brandMapper.selectByExample(null);
        //设置返回值
        PageResult pageResult = new PageResult(tbBrands.getTotal(),tbBrands.getResult());

        //返回结果
        return pageResult;
    }


    /**
     * 新增品牌
     *
     * @param brand
     */
    @Override
    public Result add(TbBrand brand) {
        //进行校验
        List<TbBrand> tbBrands = brandMapper.selectByExample(null);
        for (TbBrand tbBrand : tbBrands) {
            //如果品牌名称相同
            if (tbBrand.getName().equals(brand.getName())) {
                return new Result(false, "品牌不可重复添加");
            }
        }

        brandMapper.insert(brand);
        return new Result(true, "添加成功");
    }

    /**
     * 修改品牌先进行单个查找
     *
     * @param id
     * @return
     */
    @Override
    public TbBrand findOne(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }


    /**
     * 修改品牌
     *
     * @param brand
     * @return
     */
    @Override
    public Result update(TbBrand brand) {
        //如果是空的，不让修改
        if (brand.getName().trim().equals("") || brand.getFirstChar().trim().equals("")) {
            return new Result(false, "信息不能为空");
        } else if (brand.getFirstChar().trim().length() != 1) {
            return new Result(false, "品牌首字母只允许一个！");
        }
        brandMapper.updateByPrimaryKey(brand);
        return new Result(true, "修改成功");
    }

    /**
     * 循环删除
     * @param ids
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id:ids) {
            brandMapper.deleteByPrimaryKey(id);
        }
    }


    /**
     * 条件查询
     * @param brand
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageResult findPage(TbBrand brand, int pageNum, int pageSize) {
        //添加分页即可
        PageHelper.startPage(pageNum, pageSize);

        TbBrandExample example = new TbBrandExample();
        TbBrandExample.Criteria criteria = example.createCriteria();

        if (brand != null){
            if (brand.getName() != null && brand.getName().length() > 0) {
                criteria.andNameLike("%"+brand.getName()+"%");
            }
            if (brand.getFirstChar() !=null && brand.getFirstChar().length() >0){
                criteria.andFirstCharLike("%"+brand.getFirstChar()+"%");
            }
        }

        //强转为Page
        Page<TbBrand> tbBrands = (Page<TbBrand>) brandMapper.selectByExample(example);
        //设置返回值
        PageResult pageResult = new PageResult(tbBrands.getTotal(),tbBrands.getResult());

        //返回结果
        return pageResult;

    }

    @Override
    public List<Map> findBrandList() {
        return brandMapper.findBrandList();
    }
}
