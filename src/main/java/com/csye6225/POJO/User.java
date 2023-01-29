package com.csye6225.POJO;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@Entity
@EntityListeners(value = AuditingEntityListener.class)
@Table(name = "Users")
public class User {
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
}
