package com.jsc.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "ecommerce")
public class Product {
    private String name;
    private String desc;
    private String price;
    private String producer;
    private List<String> tags;
}
