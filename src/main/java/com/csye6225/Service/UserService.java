package com.csye6225.Service;

import com.csye6225.Exception.ChangeOthersInfoException;
import com.csye6225.Exception.GetOthersInfoException;
import com.csye6225.Util.ErrorMessage;
import com.csye6225.Util.UserHolder;
import com.csye6225.VO.UserVO;
import com.csye6225.Exception.InvalidUpdateException;
import com.csye6225.Exception.RepeatEmailException;
import com.csye6225.POJO.User;
import com.csye6225.Repository.UserRepository;
import com.csye6225.Util.UIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
public class UserService implements UserDetailsService {

    //change here
    @Autowired
    UserRepository userRepositorty;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserVO getUser(Long id){
        User user  = userRepositorty.findById(id).get();
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    public UserVO createUser(User user) {
        // prevent user change the create and update times
        user.setAccountCreated(null);
        user.setAccountUpdated(null);

        List<User> users = userRepositorty.findByUsername(user.getUsername());
        if(users!= null && users.size()>0){
            throw new RepeatEmailException(ErrorMessage.REPEAD_EMAIL + user.getUsername());
        }
        // save user
        // encode the password
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepositorty.save(user);
        // return the user information
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    public void updateUser(Long id, User user) {

        User oldUser = userRepositorty.findById(id).get();
        BeanUtils.copyProperties(user,oldUser,"createdTime","username","id");
        oldUser.setPassword(bCryptPasswordEncoder.encode(oldUser.getPassword()));
        userRepositorty.save(oldUser);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepositorty.getUserByUsername(username);
        if(user ==null){
            log.error("User not found");
            throw new UsernameNotFoundException("Username " + username +"not found");
        }else{

            // save user to thread local storage
            UserHolder.saveUser(user);
            return user;
        }

    }
}
