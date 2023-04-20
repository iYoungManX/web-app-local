package com.csye6225.Controller;

import com.csye6225.Exception.UserException.GetOthersInfoException;
import com.csye6225.Exception.UserException.InvalidUpdateException;
import com.csye6225.Util.ErrorMessage;
import com.csye6225.Util.Metrics;
import com.csye6225.Util.UserHolder;
import com.csye6225.VO.UserVO;
import com.csye6225.Exception.UserException.ChangeOthersInfoException;
import com.csye6225.POJO.User;
import com.csye6225.Service.UserService;
import com.timgroup.statsd.StatsDClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/user")
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    StatsDClient statsDClient;

    @GetMapping("/{id}")
    public UserVO getUser(@PathVariable Long id) {
        statsDClient.incrementCounter(Metrics.GET_USER);
        log.info("GET: /v1/user/{id}");
        User unAuthUser = UserHolder.getUser();
        if(!unAuthUser.getId().equals(id)){
            throw new GetOthersInfoException(ErrorMessage.GET_OTHER_INFORMATION);
        }

        return userService.getUser(id);
    }


    @PostMapping("/")
    public UserVO createUser(@RequestBody User user) {
        log.info("POST: /v1/user/");
        statsDClient.incrementCounter(Metrics.CREATE_USER);
        return userService.createUser(user);
    }

    @PutMapping ("/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody User user) {
        log.info("PUT: /v1/user/{id}");
        statsDClient.incrementCounter(Metrics.UPDATE_USER);
        User unAuthUser = UserHolder.getUser();
        if(!unAuthUser.getId().equals(id)){
            throw new ChangeOthersInfoException(ErrorMessage.CHANGE_OTHER_INFORMATION);
        }

        if(user.getUsername()!=null ||user.getAccountUpdated()!=null ||
                user.getAccountCreated()!=null){
            throw new InvalidUpdateException(ErrorMessage.INVALID_UPDATE_OTHER_INFORMATION);
        }

        userService.updateUser(id, user);
        return ResponseEntity.status(204).body("");
    }
}
