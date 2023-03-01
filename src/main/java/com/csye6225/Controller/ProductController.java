package com.csye6225.Controller;

import com.csye6225.POJO.Image;
import com.csye6225.POJO.Product;
import com.csye6225.Service.ImageService;
import com.csye6225.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/v1/product")
public class ProductController {

    @Autowired
    ProductService productService;


    @Autowired
    ImageService imageService;

    @GetMapping("/{productId}")
    public Product getProduct(@PathVariable Long productId) {
        return productService.getProduct(productId);
    }

    @PostMapping("/")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product newProduct = productService.createProduct(product);
        return ResponseEntity.ok(newProduct);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<String> updateProduct(@PathVariable Long productId,@RequestBody Product product){
        productService.updateProduct(productId,product);
        return ResponseEntity.status(204).body("Update successful");
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<String> patchProduct(@PathVariable Long productId,@RequestBody Product product){
        productService.updateProduct(productId,product);
        return ResponseEntity.status(204).body("Update successful");
    }

    @DeleteMapping("/{productId}")
    public String deleteProduct(@PathVariable Long productId){
        productService.deleteProduct(productId);
        return "Deleted successfully";
    }


    @GetMapping("/{productId}/image")
    public List<Image> getAllImages(@PathVariable Long productId){
        return imageService.getAllImagesbyProductId(productId);
    }

    @PostMapping("/{productId}/image")
    public Image createImage(@PathVariable Long productId,@RequestParam("file") MultipartFile file) throws Exception {
        return imageService.createImage(productId,file);
    }


    @GetMapping("/{productId}/image/{imageId}")
    public List<Image> getImage(@PathVariable Long productId, @PathVariable String imageId){
        return imageService.getImageById(productId,imageId);
    }

    @DeleteMapping("/{productId}/image/{imageId}")
    public void deleteImage(@PathVariable Long productId, @PathVariable String imageId){
        imageService.DeleteImageById(productId,imageId);
    }


}
