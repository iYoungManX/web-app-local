package com.csye6225.Repository;

import com.csye6225.POJO.Image;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface ImageRepository extends JpaRepository<Image, String> {
    List<Image> findByProductId(Long productId);
    List<Image> findByImageId(String imageId);

}
