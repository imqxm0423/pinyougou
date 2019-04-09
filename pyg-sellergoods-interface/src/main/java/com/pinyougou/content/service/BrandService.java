package com.pinyougou.content.service;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;
import entity.Result;

import java.util.List;
import java.util.Map;

public interface BrandService {
    List<TbBrand> findAll();

    PageResult findPage(int pageNum, int pageSize);

    Result add(TbBrand brand);

    TbBrand findOne(Long id);

    Result update(TbBrand brand);

    void delete(Long[] ids);

    //名字一样的条件查询
    PageResult findPage(TbBrand brand ,int pageNum, int pageSize);

    List<Map> findBrandList();
}
