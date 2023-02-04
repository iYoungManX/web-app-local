package com.csye6225.Service;

import com.csye6225.Exception.ProductException.CreateOrUpdateProductException;
import com.csye6225.Exception.ProductException.ProductNotExistException;
import com.csye6225.POJO.Product;
import com.csye6225.POJO.User;
import com.csye6225.Repository.ProductRepository;
import com.csye6225.Util.RandomProductFactory;
import com.csye6225.Util.UserHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static reactor.core.publisher.Mono.when;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Autowired
    RandomProductFactory randomProductFactory;

    @MockBean
    ProductRepository productRepository;


    @MockBean
    User user;

    @Autowired
    ProductService productService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void GetProductTEST() {
        Product product = randomProductFactory.getRandomProduct();
        Long productId = product.getId();
        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        assertEquals(productService.getProduct(productId), product);
    }


    @Test
    void ProductNotExistTEST() {
        Product product = randomProductFactory.getRandomProduct();
        Long productId = product.getId();
        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.empty());
        assertThrows(ProductNotExistException.class, ()-> productService.getProduct(productId));
    }

//    @Test
//    void CreateProductTEST() {
//        Product product = randomProductFactory.getRandomProduct();
//        product.setId(10L);
//        Mockito.when(productRepository.findBySku(product.getSku())).thenReturn(null);
//        Product product2 = new Product();
//        BeanUtils.copyProperties(product, product2);
//        product2.setOwnerUserId(100L);
//        Mockito.when(productRepository.save(product)).thenReturn(product2);
//        User user = new User();
//        user.setId(100L);
//        // this is how to mock the static methods
//        Mockito.mockStatic(UserHolder.class);
//        Mockito.when(UserHolder.getUser()).thenReturn(user);
//        assertEquals(product2, productService.createProduct(product));
//        Mockito.mockStatic(UserHolder.class).close();
//    }


}