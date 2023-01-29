package com.csye6225.VO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class UserVO {
    private Long id;
    private String username;

    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("account_created")
    private Date accountCreated;

    @JsonProperty("account_updated")
    private Date accountUpdated;


}
