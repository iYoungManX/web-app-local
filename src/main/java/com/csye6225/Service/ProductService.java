package com.csye6225.Service;

import com.csye6225.Exception.ProductException.CreateOrUpdateProductException;
import com.csye6225.Exception.ProductException.NoContentException;
import com.csye6225.Exception.ProductException.ProductNotExistException;
import com.csye6225.Exception.UserException.ChangeOthersInfoException;
import com.csye6225.POJO.Product;
import com.csye6225.Repository.ProductRepository;
import com.csye6225.Util.ErrorMessage;
import com.csye6225.Util.UserHolder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;


    public Product getProduct(Long productId){
        // check for the product
        Optional<Product> product = productRepository.findById(productId);
        if(product.isPresent()){
            return product.get();
        }else{
            throw new ProductNotExistException(ErrorMessage.PRODUCT_NOT_EXIST);  //400
        }

    }

    public Product createProduct(Product product) {
        // check the product quantity  400 bad request or 204 no content
        checkProduct(product);

        // check if sku is repeated   404
        List<Product> existingProducts =
                productRepository.findBySku(product.getSku());
        if(existingProducts!=null && existingProducts.size()>0){
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
                && existingProducts!=null && existingProducts.size()>0){
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
        if(product.getName().isEmpty()
                || product.getDescription().isEmpty()
                || product.getManufacturer().isEmpty()
                || product.getSku().isEmpty()
                || product.getQuantity() ==null){
            throw new NoContentException(ErrorMessage.NO_CONTENT);
        }
        // check is quantity is correct  400
        if( product.getQuantity()< 0){
            throw new CreateOrUpdateProductException(ErrorMessage.PRODUCT_QUANTITY_ERROR);
        }
    }


    private void checkAuth(Product oldProduct){
        // check is quantity is correct  404
        if(!Objects.equals(oldProduct.getOwnerUserId(), UserHolder.getUser().getId())){
            throw new ChangeOthersInfoException(ErrorMessage.CHANGE_OTHER_INFORMATION);
        }
    }


}
