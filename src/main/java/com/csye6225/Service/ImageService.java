package com.csye6225.Service;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.csye6225.Exception.ImageException.ImageNotFoundException;
import com.csye6225.Exception.UserException.ChangeOthersInfoException;
import com.csye6225.Exception.UserException.GetOthersInfoException;
import com.csye6225.POJO.Image;
import com.csye6225.POJO.Product;
import com.csye6225.Repository.ImageRepository;
import com.csye6225.Util.ErrorMessage;
import com.csye6225.Util.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
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

    private final String bucketName = System.getenv("BUCKET_NAME");
//    private String bucketName ="yao-zo";


    public List<Image> getAllImagesbyProductId(Long productId) {
        Product product = productService.getProduct(productId);
        if (!Objects.equals(product.getOwnerUserId(), UserHolder.getUser().getId())){
            throw new GetOthersInfoException(ErrorMessage.GET_OTHER_INFORMATION);
        }
        return imageRepository.findByProductId(productId);
    }


    public List<Image> getImageById(Long productId, String imageId) {
        List<Image> image = imageRepository.findByImageId(imageId);
        checkAuth(image.get(0), productId);
        if(image.size() == 0){
            throw new ImageNotFoundException(ErrorMessage.IMAGE_NOT_FOUND);
        }
        return image;
    }

    public void DeleteImageById(Long productId, String imageId) {
        Image image = getImageById(productId, imageId).get(0);
        // delete the image from amazon s3
        String fileName = image.getFileName();
        String s3BucketPath = "images/user-" + UserHolder.getUser().getId()
                + "/product-" + productId + "/"  + fileName;

        amazonS3.deleteObject(bucketName, s3BucketPath);
       //  delete from the database
        imageRepository.deleteById(imageId);
    }
    private void checkAuth(Image image, Long productId) {
        if(!Objects.equals(image.getUser().getId(), UserHolder.getUser().getId())){
            throw new ChangeOthersInfoException(ErrorMessage.CHANGE_OTHER_INFORMATION);
        }

        if(!Objects.equals(image.getProduct().getId(), productId)){
            throw new ChangeOthersInfoException(ErrorMessage.CHANGE_OTHER_INFORMATION);
        }
    }

    public Image createImage(Long productId, MultipartFile file) {
        // if product not exists, throw product not exists exception
        Product product = productService.getProduct(productId);
        if (!Objects.equals(product.getOwnerUserId(), UserHolder.getUser().getId())){
            throw new ChangeOthersInfoException(ErrorMessage.CHANGE_OTHER_INFORMATION);
        }
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
        return getImageById(productId, image.getImageId()).get(0);
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
