package com.csye6225.Controller;

import com.csye6225.Exception.GetOthersInfoException;
import com.csye6225.Exception.InvalidUpdateException;
import com.csye6225.Util.ErrorMessage;
import com.csye6225.Util.UserHolder;
import com.csye6225.VO.UserVO;
import com.csye6225.Exception.ChangeOthersInfoException;
import com.csye6225.POJO.User;
import com.csye6225.Service.UserService;
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


    @GetMapping("/{id}")
    public UserVO getUser(@PathVariable Long id) {

        User unAuthUser = UserHolder.getUser();
        if(!unAuthUser.getId().equals(id)){
            throw new GetOthersInfoException(ErrorMessage.GET_OTHER_INFORMATION);
        }

        return userService.getUser(id);
    }


    @PostMapping("/")
    public UserVO createUser(@RequestBody User user) {
        UserVO userVO = userService.createUser(user);
        return userVO;
    }

    @PutMapping ("/{id}")
    public void updateUser(@PathVariable Long id, @RequestBody User user) {

        User unAuthUser = UserHolder.getUser();
        if(!unAuthUser.getId().equals(id)){
            throw new ChangeOthersInfoException(ErrorMessage.CHANGE_OTHER_INFORMATION);
        }

        if(!user.getUsername().equals(unAuthUser.getUsername()) ||
                user.getAccountUpdated()!=null ||
                user.getAccountCreated()!=null){
            throw new InvalidUpdateException(ErrorMessage.INVALID_UPDATE_OTHER_INFORMATION);
        }

        userService.updateUser(id, user);
    }
}
