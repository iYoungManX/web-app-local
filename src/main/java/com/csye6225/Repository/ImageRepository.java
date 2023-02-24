package com.csye6225.Repository;

import com.csye6225.POJO.Image;
import com.csye6225.POJO.Product;
import com.csye6225.VO.ImageVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    @Query("SELECT new com.csye6225.VO.ImageVO(i.imageId, i.user.id, i.fileName,i.dateCreated,i.s3BucketPath) FROM Image i WHERE i.product.id = :productId")
    List<ImageVO> findByProductId(@Param("productId") Long productId);

    @Query("SELECT new com.csye6225.VO.ImageVO(i.imageId, i.user.id, i.fileName,i.dateCreated,i.s3BucketPath) FROM Image i WHERE i.imageId = :id")
    List<ImageVO> getImageById(@Param("id") Long id);
}
