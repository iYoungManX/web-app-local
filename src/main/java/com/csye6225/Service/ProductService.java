package com.csye6225.Service;

import com.csye6225.Exception.ProductException.CreateOrUpdateProductException;
import com.csye6225.Exception.ProductException.NoContentException;
import com.csye6225.Exception.ProductException.ProductNotExistException;
import com.csye6225.Exception.UserException.ChangeOthersInfoException;
import com.csye6225.POJO.Product;
import com.csye6225.POJO.User;
import com.csye6225.Repository.ProductRepository;
import com.csye6225.Util.ErrorMessage;
import com.csye6225.Util.UserHolder;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class ProductService {

    @Autowired
    ProductRepository productRepository;


    public Product getProduct(Long productId){
        // check for the product
        Optional<Product> product = productRepository.findById(productId);
        if(product.isPresent()){
//            log.info("User {}: Get the product {}", UserHolder.getUser().getId(), productId);
            return product.get();
        }else{
//            log.warn("User {}: The product doesn't exist ",UserHolder.getUser().getId());
            throw new ProductNotExistException(ErrorMessage.PRODUCT_NOT_EXIST);  //400
        }

    }

    public Product createProduct(Product product) {
        // check the product quantity  400 bad request or 204 no content
        checkProduct(product);

        // check if sku is repeated   404
        List<Product> existingProducts =
                productRepository.findBySku(product.getSku());
        if(!CollectionUtils.isEmpty(existingProducts)){
            throw new CreateOrUpdateProductException(ErrorMessage.REPEAT_SKU);
        }

        // set the user_owner_id
        product.setOwnerUserId(UserHolder.getUser().getId());
        productRepository.save(product);
        return product;
    }

    public void updateProduct(Long productId,Product product) {
        // if product not exist, return 400 bad request
        Product oldProduct = getProduct(productId);
        // auth id is different from product owner_id  return unauthorized 403
        checkAuth(oldProduct);
        // check the product quantity  400 bad request or 204 no content
        checkProduct(product);
        // check if sku is repeated   400 bad request
        List<Product> existingProducts = productRepository.findBySku(product.getSku());
        if(!oldProduct.getSku().equals(product.getSku())
                && !CollectionUtils.isEmpty(existingProducts)){
            throw new CreateOrUpdateProductException(ErrorMessage.REPEAT_SKU);
        }
        // update the product
        BeanUtils.copyProperties(product, oldProduct, "id","ownerUserId","dateAdded");
        productRepository.save(oldProduct);
    }




    public void deleteProduct(Long productId) {
        // if product not exist, return 400 bad request
        Product oldProduct = getProduct(productId);
        // auth id is different from product owner id  return unauthorized 403
        checkAuth(oldProduct);
        // delete the product
        productRepository.delete(oldProduct);
    }


    private void checkProduct(Product product){
        // check if all field exists  no content 204

        if( StringUtils.isEmpty(product.getName())
                || StringUtils.isEmpty(product.getDescription())
                || StringUtils.isEmpty(product.getManufacturer())
                || StringUtils.isEmpty(product.getSku())
                || product.getQuantity() ==null){
            log.warn("Some required fields of the product is missing");
            throw new NoContentException(ErrorMessage.NO_CONTENT);
        }
        log.info("The product has all the required fields");
        // check is quantity is correct  400
        if( product.getQuantity()< 0){
            log.warn("The product quantity is less than 0");
            throw new CreateOrUpdateProductException(ErrorMessage.PRODUCT_QUANTITY_ERROR);
        }
    }


    private void checkAuth(Product oldProduct){
        // check is quantity is correct  404
        if(!Objects.equals(oldProduct.getOwnerUserId(), UserHolder.getUser().getId())){
            log.warn("User {} is trying to the get User {} 's product with ProductId: {} "
                    ,UserHolder.getUser().getId(), oldProduct.getOwnerUserId(), oldProduct.getId());
            throw new ChangeOthersInfoException(ErrorMessage.CHANGE_OTHER_INFORMATION);
        }
    }


}
