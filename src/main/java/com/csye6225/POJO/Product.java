package com.csye6225.POJO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;



@Data
@Entity
@EntityListeners(value = AuditingEntityListener.class)
@Table(name = "Product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String description;
    private String sku;
    private String manufacturer;
    private Long quantity;
    @CreatedDate
    @JsonProperty("date_added")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String dateAdded;
    @LastModifiedDate
    @JsonProperty("date_last_updated")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String dateLastUpdated;
    @JsonProperty("owner_userid")
    private Long ownerUserId;
}
