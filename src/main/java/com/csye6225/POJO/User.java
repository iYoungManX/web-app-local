package com.csye6225.POJO;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;

import lombok.Data;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Data
@Entity
@EntityListeners(value = AuditingEntityListener.class)
@Table(name = "Users")
public class User implements UserDetails {
    @Id
    private Long id;
    @Email(message = "Invalid email format")
    private String username;
    private String password;

    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")

    private String lastName;
    @CreatedDate
    @JsonProperty("account_created")

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date accountCreated;
    @LastModifiedDate
    @JsonProperty("account_updated")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date accountUpdated;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("User"));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
