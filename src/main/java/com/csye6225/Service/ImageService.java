package com.csye6225.Service;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.csye6225.Exception.ImageException.ImageNotFoundException;
import com.csye6225.Exception.UserException.ChangeOthersInfoException;
import com.csye6225.POJO.Image;
import com.csye6225.Repository.ImageRepository;
import com.csye6225.Util.ErrorMessage;
import com.csye6225.Util.UserHolder;
import com.csye6225.VO.ImageVO;
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
import java.util.Optional;

@Service
@Slf4j
public class ImageService {

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${bucket.name}")
    private String bucketName;


    public List<ImageVO> getAllImagesbyProductId(Long productId) {
        return imageRepository.findByProductId(productId);
    }


    public List<ImageVO> getImageById(Long imageId) {
        List<ImageVO> image = imageRepository.getImageById(imageId);
        if(image == null || image.size() == 0){
            throw new ImageNotFoundException(ErrorMessage.IMAGE_NOT_FOUND);
        }
        return image;
    }

    public void DeleteImageById(Long imageId) {
        List<ImageVO> imageVO = getImageById(imageId);
        checkAuth(imageVO.get(0));
        imageRepository.deleteById(imageId);
    }
    private void checkAuth(ImageVO imageVO) {
        if(!Objects.equals(imageVO.getUserId(), UserHolder.getUser().getId())){
            throw new ChangeOthersInfoException(ErrorMessage.CHANGE_OTHER_INFORMATION);
        }
    }

    public ImageVO createImage(Long productId, MultipartFile file) throws Exception {

        byte[] imageData = file.getBytes();
        String fileName = file.getOriginalFilename();
        String s3BucketPath = "images/user:" + UserHolder.getUser().getId()
                + "/product:" + productId + "/"  + fileName;

        // Upload the image to the S3 bucket
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(imageData.length);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,
                s3BucketPath, new ByteArrayInputStream(imageData), metadata);
        amazonS3.putObject(putObjectRequest);
        String url = amazonS3.getUrl(bucketName, s3BucketPath).toString();

        // update the database
        Image image = new Image();
        image.setFileName(fileName);
        image.setS3BucketPath(url);
        imageRepository.save(image);
        return null;
    }
}
