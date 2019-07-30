package com.itheima.service;

import com.itheima.mapper.TbBrandMapper;
import com.itheima.pojo.TbBrand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Date: 2019/7/20
 * @Author: itheima
 */
@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private TbBrandMapper brandMapper;


    @Override
    // 查询一个
    public TbBrand findById(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }



}
