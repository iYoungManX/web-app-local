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


@Service
@Slf4j
public class UserService implements UserDetailsService {
    //change here
    @Autowired
    UserRepository userRepositorty;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserVO getUser(Long id){

        User unAuthUser = UserHolder.getUser();
        if(!unAuthUser.getId().equals(id)){
            throw new GetOthersInfoException(ErrorMessage.GET_OTHER_INFORMATION);
        }

        User user = userRepositorty.findById(id).get();
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    public UserVO createUser(User user) {
        // prevent user change the create and update times
        user.setAccountCreated(null);
        user.setAccountUpdated(null);
        // generate user id
        Long id = UIDUtil.nextId();
        user.setId(id);

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
        User newUser = userRepositorty.findById(id).get();
        BeanUtils.copyProperties(newUser, userVO);
        return userVO;
    }

    public void updateUser(Long id, User user) {

        User unAuthUser = UserHolder.getUser();
        if(!unAuthUser.getId().equals(id)){
            throw new ChangeOthersInfoException(ErrorMessage.CHANGE_OTHER_INFORMATION);
        }

        if(user.getUsername()!= null ||
                user.getAccountUpdated()!=null ||
                    user.getAccountCreated()!=null){
            throw new InvalidUpdateException(ErrorMessage.INVALID_UPDATE_OTHER_INFORMATION);
        }



        User oldUser = userRepositorty.findById(id).get();
        Date createdTime = oldUser.getAccountCreated();
        String username = oldUser.getUsername();
        BeanUtils.copyProperties(user,oldUser);
        oldUser.setId(id);
        oldUser.setAccountCreated(createdTime);
        oldUser.setUsername(username);
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
