package com.csye6225.Config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class AmazonS3Config {

    Map<String, String> env = System.getenv();

    private final String accessKey = System.getenv("AWS_ACCESS_KEY_ID");
    private final String secretKey = System.getenv("AWS_SECRET_ACCESS_KEY");
    private String region = "us-west-2";

    @Bean
    public AmazonS3 amazonS3() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();

        return amazonS3;
    }

}