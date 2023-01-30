package com.csye6225.Repository;

import com.csye6225.POJO.User;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByUsername(String username);

    User getUserByUsername(String username);
}