package com.csye6225.Service;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.csye6225.Exception.ImageException.ImageNotFoundException;
import com.csye6225.Exception.UserException.ChangeOthersInfoException;
import com.csye6225.POJO.Image;
import com.csye6225.POJO.Product;
import com.csye6225.Repository.ImageRepository;
import com.csye6225.Util.ErrorMessage;
import com.csye6225.Util.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class ImageService {

    @Autowired
    ImageRepository imageRepository;


    @Autowired
    ProductService productService;
    @Autowired
    private AmazonS3 amazonS3;

    private String bucketName = System.getenv("BUCKET_NAME");


    public List<Image> getAllImagesbyProductId(Long productId) {
        return imageRepository.findByProductId(productId);
    }


    public List<Image> getImageById(String imageId) {
        List<Image> image = imageRepository.findByImageId(imageId);
        if(image == null || image.size() == 0){
            throw new ImageNotFoundException(ErrorMessage.IMAGE_NOT_FOUND);
        }
        return image;
    }

    public void DeleteImageById(String imageId) {
        Image image = getImageById(imageId).get(0);
        checkAuth(image);

        // delete the image from amazon s3
        Long productId = image.getProduct().getId();
        String fileName = image.getFileName();
        String s3BucketPath = "images/user-" + UserHolder.getUser().getId()
                + "/product-" + productId + "/"  + fileName;

        amazonS3.deleteObject(bucketName, s3BucketPath);
       //  delete from the database
        imageRepository.deleteById(imageId);
    }
    private void checkAuth(Image image) {
        if(!Objects.equals(image.getUserId(), UserHolder.getUser().getId())){
            throw new ChangeOthersInfoException(ErrorMessage.CHANGE_OTHER_INFORMATION);
        }
    }

    public Image createImage(Long productId, MultipartFile file) {
        // if product not exists, throw product not exists exception
        Product product = productService.getProduct(productId);

        // upload image to S3
        String url = null;
        try {
            url = uploadImageToS3(productId, file);
            log.info("Uploading image Successfully");
        } catch (Exception e) {
            throw new RuntimeException("Upload Failed!!");
        }

        // update the database
        Image image = new Image();
        image.setImageId(UUID.randomUUID().toString());
        image.setFileName(file.getOriginalFilename());
        image.setS3BucketPath(url);
        image.setUser(UserHolder.getUser());
        image.setProduct(product);
        imageRepository.save(image);
        return getImageById(image.getImageId()).get(0);
    }

    private String uploadImageToS3( Long productId, MultipartFile file) throws Exception {
        byte[] imageData = file.getBytes();
        String fileName = file.getOriginalFilename();
        String s3BucketPath = "images/user-" + UserHolder.getUser().getId()
                + "/product-" + productId + "/"  + fileName;
        // Upload the image to the S3 bucket
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(imageData.length);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,
                s3BucketPath, new ByteArrayInputStream(imageData), metadata);
        amazonS3.putObject(putObjectRequest);
        return amazonS3.getUrl(bucketName, s3BucketPath).toString();
    }


}
