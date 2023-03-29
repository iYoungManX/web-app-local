package com.csye6225.Controller;

import com.csye6225.POJO.Image;
import com.csye6225.POJO.Product;
import com.csye6225.Service.ImageService;
import com.csye6225.Service.ProductService;
import com.csye6225.Util.Metrics;
import com.timgroup.statsd.StatsDClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/v1/product")
public class ProductController {
    @Autowired
    ProductService productService;

    @Autowired
    StatsDClient statsDClient;

    @Autowired
    ImageService imageService;

    @GetMapping("/{productId}")
    public Product getProduct(@PathVariable Long productId) {
        log.info("GET: /v1/product/{productId}");
        statsDClient.incrementCounter(Metrics.GET_PRODUCT);
        return productService.getProduct(productId);
    }

    @PostMapping("/")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        log.info("POST: /v1/product/");
        statsDClient.incrementCounter(Metrics.CREATE_PRODUCT);
        Product newProduct = productService.createProduct(product);
        return ResponseEntity.ok(newProduct);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<String> updateProduct(@PathVariable Long productId,@RequestBody Product product){
        log.info("PUT: /v1/product/{productId}");
        statsDClient.incrementCounter(Metrics.UPDATE_PRODUCT);
        productService.updateProduct(productId,product);
        return ResponseEntity.status(204).body("Update successful");
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<String> patchProduct(@PathVariable Long productId,@RequestBody Product product){
        log.info("PATCH: /v1/product/{productId}");
        statsDClient.incrementCounter(Metrics.UPDATE_PRODUCT);
        productService.updateProduct(productId,product);
        return ResponseEntity.status(204).body("Update successful");
    }

    @DeleteMapping("/{productId}")
    public String deleteProduct(@PathVariable Long productId){
        log.info("DELETE: /v1/product/{productId}");
        statsDClient.incrementCounter(Metrics.DELETE_PRODUCT);
        productService.deleteProduct(productId);
        return "Deleted successfully";
    }


    @GetMapping("/{productId}/image")
    public List<Image> getAllImages(@PathVariable Long productId){
        log.info("GET: /v1/product/{productId}/image");
        statsDClient.incrementCounter(Metrics.GET_ALL_IMAGES);
        return imageService.getAllImagesbyProductId(productId);
    }

    @PostMapping("/{productId}/image")
    public Image createImage(@PathVariable Long productId,@RequestParam("file") MultipartFile file) throws Exception {
        log.info("POST: /v1/product/{productId}/image");
        statsDClient.incrementCounter(Metrics.CREATE_IMAGE);
        return imageService.createImage(productId,file);
    }


    @GetMapping("/{productId}/image/{imageId}")
    public List<Image> getImage(@PathVariable Long productId, @PathVariable String imageId){
        log.info("GET: /v1/product/{productId}/image/{imageId}");
        statsDClient.incrementCounter(Metrics.GET_IMAGE);
        return imageService.getImageById(productId,imageId);
    }

    @DeleteMapping("/{productId}/image/{imageId}")
    public void deleteImage(@PathVariable Long productId, @PathVariable String imageId){
        log.info("DELETE: /v1/product/{productId}/image/{imageId}");
        statsDClient.incrementCounter(Metrics.DELETE_IMAGE);
        imageService.DeleteImageById(productId,imageId);
    }
}
