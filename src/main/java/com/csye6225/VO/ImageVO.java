package com.csye6225.VO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageVO {
    @JsonProperty("image_id")
    private Long imageId;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("file_name")
    private String fileName;

    @JsonProperty("date_created")
    private Date dateCreated;
    @JsonProperty("s3_bucket_path")
    private String s3BucketPath;
}
