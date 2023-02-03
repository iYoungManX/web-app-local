package com.csye6225.Controller;

import com.csye6225.POJO.Product;
import com.csye6225.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/product")
public class ProductController {

    @Autowired
    ProductService productService;

    @GetMapping("/{productId}")
    public Product getProduct(@PathVariable Long productId) {
        return productService.getProduct(productId);
    }

    @PostMapping("/")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product newProduct = productService.createProduct(product);
        return ResponseEntity.status(201).body(newProduct);
    }

    @PutMapping("/{productId}")
    public String updateProduct(@PathVariable Long productId,@RequestBody Product product){
        productService.updateProduct(productId,product);
        return "Updated successfully";
    }

    @PatchMapping("/{productId}")
    public String patchProduct(@PathVariable Long productId,@RequestBody Product product){
        productService.updateProduct(productId,product);
        return "Updated successfully";
    }

    @DeleteMapping("/{productId}")
    public String deleteProduct(@PathVariable Long productId){
        productService.deleteProduct(productId);
        return "Deleted successfully";
    }



}
