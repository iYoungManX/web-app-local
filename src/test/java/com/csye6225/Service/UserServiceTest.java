//package com.csye6225.Service;
//
//import com.csye6225.Repository.UserRepository;
//import com.csye6225.Util.RandomUserFactory;
//import com.csye6225.POJO.User;
//import com.csye6225.VO.UserVO;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Import;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//@SpringBootTest
//@AutoConfigureMockMvc
//@ExtendWith(MockitoExtension.class)
//@Import(UserService.class)
//class UserServiceTest {
//
//    @MockBean
//    UserRepository userRepository;
//
//    @Autowired
//    UserService userService;
//
//    @Autowired
//    RandomUserFactory randomUserFactory;
//
//    @BeforeEach
//    void setUp() {
//    }
//
//    @AfterEach
//    void tearDown() {
//    }
//
//    @Test
//    @DisplayName("Get User by ID test-Service")
//    void testGetUser() {
//        User user = new User();
//        BeanUtils.copyProperties(randomUserFactory.getRandomUser(), user);
//        Long id = 12345L;
//        user.setId(id);
//        UserVO userVO = new UserVO();
//        BeanUtils.copyProperties(user, userVO);
//        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));
//        assertEquals(userService.getUser(id), userVO);
//    }
//
//    @Test
//    void createUser() {
//    }
//
//    @Test
//    void updateUser() {
//    }
//
//    @Test
//    void loadUserByUsername() {
//    }
//}