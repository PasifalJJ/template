package com.itheima.controller;

import com.itheima.pojo.TbBrand;
import com.itheima.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: itheima
 */
@RestController
public class BrandController {
    @Autowired
    private BrandService brandService;

    @RequestMapping("/findById")
    public TbBrand findById(Long id) {
        return brandService.findById(id);
    }


}
