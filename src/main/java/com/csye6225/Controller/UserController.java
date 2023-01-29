package com.csye6225.Controller;

import com.csye6225.Util.ErrorMessage;
import com.csye6225.VO.UserVO;
import com.csye6225.Exception.ChangeOthersInfoException;
import com.csye6225.POJO.User;
import com.csye6225.Service.UserService;
import com.csye6225.Util.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/user")
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    TokenUtil tokenUtil;



    @GetMapping("/{id}")
    public UserVO getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }


    @PostMapping("/")
    public UserVO createUser(@RequestBody User user, HttpServletResponse response) {
        UserVO userVO = userService.createUser(user);
        String token = tokenUtil.getToken(userVO.getId(), userVO.getUsername());
        response.addHeader("token", token);
        return userVO;
    }

    @PutMapping("/{id}")
    public void updateUser(@PathVariable Long id, @RequestBody User user, HttpServletRequest request) {
        Map<String, String> token = tokenUtil.parseToken( request.getHeader("token"));
        if(!id.toString().equals(token.get("id"))){
            throw new ChangeOthersInfoException(ErrorMessage.CHANGE_OTHER_INFORMATION);
        }
        userService.updateUser(id, user);
    }
}
