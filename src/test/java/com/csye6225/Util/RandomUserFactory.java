package com.csye6225.Util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.csye6225.Util.User;
import com.github.javafaker.Faker;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Base64;
import java.util.Random;


@Component
public class RandomUserFactory {
    private final Random random = new Random();
    private final Faker faker = new Faker();

    @Autowired
    MockMvc mockMvc;


    public User getRandomUser(){
        User user = new User();
        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());
        user.setUsername(faker.name().lastName()+random.nextInt()+ "@yahoo.com");
        user.setPassword(faker.internet().password());
        return user;
    }


    public String parseUserToJson(User user){
        SerializeConfig config = new SerializeConfig();
        config.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
        return JSON.toJSONString(user, config);
    }


    public MockHttpServletResponse sendCreateUserRequest(String url, String jsonbody) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post(url)
                .content(jsonbody).contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

    }

    public String getAuthToken(String username, String password) {
        String encoding = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        return "Basic " + encoding;
    }
}
