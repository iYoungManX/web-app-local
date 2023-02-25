package com.csye6225.POJO;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@Entity
@EntityListeners(value = AuditingEntityListener.class)
@Table(name = "image")
public class Image {

    @Id
    @JsonProperty("image_id")
    private String imageId;
    @JsonProperty("file_name")
    private String fileName;

    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("date_created")
    private Date dateCreated;

    @JsonProperty("s3_bucket_path")
    @Column(name = "s3_bucket_path")
    private String s3BucketPath;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    @JoinColumn(name = "product_id")
    private Product product;


//    @JsonProperty("user_id")
//    public Long getUserId(){
//        return user.getId();
//    }


    @JsonProperty("product_id")
    public Long getProductId(){
        return product.getId();
    }
}
