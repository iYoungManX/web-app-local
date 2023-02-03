package com.csye6225.Controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.csye6225.POJO.Product;
import com.csye6225.Util.RandomProductFactory;
import com.csye6225.Util.RandomUserFactory;
import com.csye6225.Util.User;
import com.csye6225.VO.UserVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    MockMvc mockMvc;


    @Autowired
    RandomProductFactory randomProductFactory;

    @Autowired
    RandomUserFactory randomUserFactory;

    String token;

    Random random;
    @BeforeEach
    void setUp() throws Exception {
        random = new Random();
        User user = randomUserFactory.getRandomUser();
        String jsonbody = randomUserFactory.parseUserToJson(user);
        randomUserFactory.sendCreateUserRequest("/v1/user/", jsonbody);
        this.token = randomUserFactory.getAuthToken(user.getUsername(), user.getPassword());
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void TestCreateProduct() throws Exception {
        Product product = randomProductFactory.getRandomProduct();
        String jsonbody =randomProductFactory.parseProductToJson(product);
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/product/")
                        .content(jsonbody).contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", this.token))
                .andExpect(MockMvcResultMatchers.status().is(201))
                .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("name").value(product.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("description").value(product.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("sku").value(product.getSku()))
                .andExpect(MockMvcResultMatchers.jsonPath("manufacturer").value(product.getManufacturer()))
                .andExpect(MockMvcResultMatchers.jsonPath("quantity").value(product.getQuantity()))
                .andExpect(MockMvcResultMatchers.jsonPath("date_added").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("date_last_updated").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("owner_userid").isNotEmpty())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }


    @Test
    public void TestGetProduct() throws Exception {
        Product product = randomProductFactory.getRandomProduct();
        String jsonbody = randomProductFactory.parseProductToJson(product);
        MockHttpServletResponse response = randomProductFactory
                .sendCreateProductRequest("/v1/product/", jsonbody, this.token);
        Product product2 = JSON.parseObject(response.getContentAsString(), Product.class );
//        System.out.println("===========================");
//        System.out.println(response.getContentAsString());
//        System.out.println("===========================");
//        System.out.println(product2);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/v1/product/"+ product2.getId().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(product2.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("name").value(product2.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("description").value(product2.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("sku").value(product2.getSku()))
                .andExpect(MockMvcResultMatchers.jsonPath("manufacturer").value(product2.getManufacturer()))
                .andExpect(MockMvcResultMatchers.jsonPath("quantity").value(product2.getQuantity()))
                .andExpect(MockMvcResultMatchers.jsonPath("date_added").value(product2.getDateAdded()))
                .andExpect(MockMvcResultMatchers.jsonPath("date_last_updated").value(product2.getDateLastUpdated()))
                .andExpect(MockMvcResultMatchers.jsonPath("owner_userid").value(product2.getOwnerUserId()))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse();
    }


    @Test
    public void TestUpdateProduct() throws Exception {
        Product product = randomProductFactory.getRandomProduct();
        String jsonbody = randomProductFactory.parseProductToJson(product);
        MockHttpServletResponse response = randomProductFactory
                .sendCreateProductRequest("/v1/product/", jsonbody, this.token);
        Product product2 = JSON.parseObject(response.getContentAsString(), Product.class );
//        System.out.println(product2);
        product2.setDescription("This is super ridiculous");
        jsonbody = randomProductFactory.parseProductToJson(product2);
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/v1/product/"+ product2.getId().toString())
                        .content(jsonbody).contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", this.token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse();

        // perform another get to see if it is correct
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/v1/product/"+ product2.getId().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(product2.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("name").value(product2.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("description").value(product2.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("sku").value(product2.getSku()))
                .andExpect(MockMvcResultMatchers.jsonPath("manufacturer").value(product2.getManufacturer()))
                .andExpect(MockMvcResultMatchers.jsonPath("quantity").value(product2.getQuantity()))
                .andExpect(MockMvcResultMatchers.jsonPath("date_added").value(product2.getDateAdded()))
                .andExpect(MockMvcResultMatchers.jsonPath("date_last_updated").value(product2.getDateLastUpdated()))
                .andExpect(MockMvcResultMatchers.jsonPath("owner_userid").value(product2.getOwnerUserId()))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse();
    }

    @Test
    public void TestDeleteProduct() throws Exception {
        Product product = randomProductFactory.getRandomProduct();
        String jsonbody = randomProductFactory.parseProductToJson(product);
        MockHttpServletResponse response = randomProductFactory
                .sendCreateProductRequest("/v1/product/", jsonbody, this.token);

        Product product2 = JSON.parseObject(response.getContentAsString(), Product.class );

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/v1/product/"+ product2.getId().toString())
                        .header("Authorization", this.token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse();

        // another get to if check is the product still exists
        // perform another get to see if it is correct
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/v1/product/"+ product2.getId().toString()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Product not exist"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse();

    }


}