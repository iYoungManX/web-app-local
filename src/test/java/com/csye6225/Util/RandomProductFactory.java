package com.csye6225.Util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.csye6225.POJO.Product;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Random;

@Configuration
public class RandomProductFactory {

    private final Random random = new Random();
    private final Faker faker = new Faker();

    @Autowired
    MockMvc mockMvc;


    public Product getRandomProduct(){
        Product product = new Product();
        product.setName(faker.food().ingredient());
        product.setDescription(faker.lorem().paragraph());
        product.setSku(faker.random().toString()+random.nextInt());
        product.setManufacturer(faker.company().name());
        product.setQuantity(Math.abs(faker.random().nextLong(300)));
        return product;
    }


    public String parseProductToJson(Product product){
        SerializeConfig config = new SerializeConfig();
        config.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
        return JSON.toJSONString(product, config);
    }


    public MockHttpServletResponse sendCreateProductRequest(String url, String jsonbody, String token) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post("/v1/product/")
                .content(jsonbody).contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)).andReturn().getResponse();

    }
}
