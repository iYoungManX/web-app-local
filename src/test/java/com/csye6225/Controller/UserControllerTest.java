package com.csye6225.Controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.csye6225.Util.ErrorMessage;
import com.csye6225.VO.UserVO;
import com.csye6225.Util.User;
import com.csye6225.Util.RandomUserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;


    @Autowired
    RandomUserFactory randomUserFactory;

    String token;

    Long id;

    Random random;

    @BeforeEach
    void setUp() {
       random = new Random();
    }

    @Test
    @DisplayName("Test Create User (correct email)")
    public void testCreateUser() throws Exception{


        User user = randomUserFactory.getRandomUser();
        System.out.println(user.getUsername());
        String jsonbody =randomUserFactory.parseUserToJson(user);
        System.out.println(jsonbody);

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/user/")
                        .content(jsonbody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("username").value(user.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("first_name").value(user.getFirstName()))
                .andExpect(MockMvcResultMatchers.jsonPath("last_name").value(user.getLastName()))
                .andExpect(MockMvcResultMatchers.jsonPath("account_created").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("account_updated").isNotEmpty())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    @DisplayName("RepeatEmailTest")
    public void testRepeatEmail() throws Exception {

        User user = randomUserFactory.getRandomUser();
        String jsonbody = randomUserFactory.parseUserToJson(user);
        randomUserFactory.sendCreateUserRequest("/v1/user/", jsonbody);

        // create a user witht same email
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/user/")
                        .content(jsonbody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(ErrorMessage.REPEAD_EMAIL+ user.getUsername()))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }



//    @Test
//    @DisplayName("InvalidEmailTest")
//    public void testInvalidEmail() throws Exception {
//        User user = randomUserFactory.getRandomUser();
//        user.setUsername("someinvalidemail");
//        String jsonbody = randomUserFactory.parseUserToJson(user);
//        mockMvc.perform(MockMvcRequestBuilders.post("/v1/user/")
//                        .content(jsonbody).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isBadRequest())
//                .andExpect(MockMvcResultMatchers.content().string(ErrorMessage.INVALID_EMAIL))
//                .andDo(MockMvcResultHandlers.print())
//                .andReturn();
//    }


    @Test
    @DisplayName("Get users information with token")
    public void testGetUsers() throws Exception {
        User user = randomUserFactory.getRandomUser();
        String jsonbody = randomUserFactory.parseUserToJson(user);
        MockHttpServletResponse response = randomUserFactory.sendCreateUserRequest("/v1/user/", jsonbody);
        this.token = randomUserFactory.getAuthToken(user.getUsername(), user.getPassword());
        this.id = JSON.parseObject(response.getContentAsString(), UserVO.class, Feature.DisableFieldSmartMatch ).getId();

        mockMvc.perform(MockMvcRequestBuilders
                .get("/v1/user/"+ this.id)
                .header("Authorization", this.token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(this.id.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("username").value(user.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("first_name").value(user.getFirstName()))
                .andExpect(MockMvcResultMatchers.jsonPath("last_name").value(user.getLastName()))
                .andExpect(MockMvcResultMatchers.jsonPath("account_created").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("account_updated").isNotEmpty())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse();
    }


    @Test
    @DisplayName("Get users information without token")
    public void testGetUsersWithoutToken() throws Exception {
        User user = randomUserFactory.getRandomUser();
        String jsonbody = randomUserFactory.parseUserToJson(user);
        MockHttpServletResponse response = randomUserFactory.sendCreateUserRequest("/v1/user/", jsonbody);

        this.id = JSON.parseObject(response.getContentAsString(), UserVO.class, Feature.DisableFieldSmartMatch ).getId();

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/v1/user/"+ this.id))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    @DisplayName("Get users information with wrong token")
    public void testGetUsersWithWrongToken() throws Exception {
        User user = randomUserFactory.getRandomUser();
        String jsonbody = randomUserFactory.parseUserToJson(user);
        MockHttpServletResponse response = randomUserFactory.sendCreateUserRequest("/v1/user/", jsonbody);

        this.id = JSON.parseObject(response.getContentAsString(), UserVO.class, Feature.DisableFieldSmartMatch ).getId();
        this.token =  randomUserFactory.getAuthToken(user.getUsername(), user.getPassword());;
        String wrongToken = this.token.substring(1);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/v1/user/"+ this.id)
                .header("Authorization", wrongToken))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }



    @Test
    @DisplayName("InvalidUpdate")
    public void testInvalidUpdate() throws Exception {
        User user = randomUserFactory.getRandomUser();
        String jsonbody = randomUserFactory.parseUserToJson(user);
        MockHttpServletResponse response = randomUserFactory.sendCreateUserRequest("/v1/user/", jsonbody);

        this.id = JSON.parseObject(response.getContentAsString(), UserVO.class, Feature.DisableFieldSmartMatch ).getId();
        this.token =  randomUserFactory.getAuthToken(user.getUsername(), user.getPassword());;


        user.setUsername("john@doeemail.com"+ random.nextInt());
        jsonbody = randomUserFactory.parseUserToJson(user);
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/v1/user/"+ this.id)
                        .header("Authorization", this.token)
                        .content(jsonbody).contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Only first name, last name and password can be updated"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    @DisplayName("Don't return password")
    public void testNotReturnPassword() throws Exception {
        User user = randomUserFactory.getRandomUser();
        String jsonbody = randomUserFactory.parseUserToJson(user);
        MockHttpServletResponse response = randomUserFactory.sendCreateUserRequest("/v1/user/", jsonbody);

        this.token =  randomUserFactory.getAuthToken(user.getUsername(), user.getPassword());

        this.id = JSON.parseObject(response.getContentAsString(), UserVO.class, Feature.DisableFieldSmartMatch ).getId();

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/v1/user/"+ this.id)
                        .header("Authorization", this.token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("password").doesNotExist())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }



    @Test
    @DisplayName("Change others inforamtion tests")
    public void testChangeOtherInformation() throws Exception {
        // create user 1
        User user1 = randomUserFactory.getRandomUser();
        String jsonbody1 = randomUserFactory.parseUserToJson(user1);
        MockHttpServletResponse response1 = randomUserFactory.sendCreateUserRequest("/v1/user/", jsonbody1);

        // create user 2
        User user2 = randomUserFactory.getRandomUser();
        String jsonbody2 = randomUserFactory.parseUserToJson(user2);
        MockHttpServletResponse response2 = randomUserFactory.sendCreateUserRequest("/v1/user/", jsonbody2);

        // get user 1's token
        String token1 =  randomUserFactory.getAuthToken(user1.getUsername(), user1.getPassword());

        Long id2 = JSON.parseObject(response2.getContentAsString(), UserVO.class, Feature.IgnoreNotMatch ).getId();
        user1.setUsername("john@doil.com"+ random.nextInt());
        jsonbody1 = randomUserFactory.parseUserToJson(user1);
        // use user1 1's token to get user 2's information
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/v1/user/"+ id2)
                        .header("Authorization", token1)
                        .content(jsonbody1).contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().string(ErrorMessage.CHANGE_OTHER_INFORMATION))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

    }

















}