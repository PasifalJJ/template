package com.jsc.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Test
    public void getAllTest() {
        productService.getAll();
    }

    @Test
    public void getAllEs() {
        productService.getAllEs();
    }

    @Test
    public void queryForPage() {
        productService.queryForPage();
    }

    @Test
    public void queryForPageEs() {
        productService.queryForPageEs();
    }

    @Test
    public void queryWithRange() {
        productService.queryWithRange();
    }

    @Test
    public void queryWithHighLight() {
        productService.queryWithHighLight();
    }

    @Test
    public void queryWithAgg() {
        productService.queryWithAgg();
    }

    @Test
    public void queryAggWithAgg() {
        productService.queryAggWithAgg();
    }

    @Test
    public void queryWithRangeAggSubAgg() {
        productService.queryWithRangeAggSubAgg();
    }

    @Test
    public void insertEs() throws Exception {
        productService.insertEs();
    }
}
