package com.productservice.controller;

import com.productservice.model.MultipleProduct;
import com.productservice.model.Product;
import com.productservice.repo.ProductRepository;
import com.productservice.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/products")
@CrossOrigin("*")
public class ProductController {

    private static final Logger LOGGER = Logger.getLogger(ProductController.class.getName());

    @Autowired
    ProductService productService;

    @Autowired
    ProductRepository productRepository;

    // Endpoint to create a new product
    @PostMapping
    public Product createProduct(@Valid @RequestBody Product product, BindingResult bindingResult) {
        LOGGER.log(Level.INFO, "POST request received to create a new product: {0}", product.getName());
        return productService.createProduct(product, bindingResult);
    }

    // Endpoint to create multiple products
    @PostMapping("/addAll")
    public List<Product> createMultipleProducts(@Valid @RequestBody MultipleProduct multipleProduct, BindingResult bindingResult) {
        LOGGER.log(Level.INFO, "POST request received to create multiple products");
        productService.validationError(bindingResult);
        return productService.createMultipleProducts(multipleProduct);
    }

    // Endpoint to get All products
   @GetMapping
   public List<Product> getAllProduct(){
       return productService.getAllProducts();
   }



    // Endpoint to get a specific product by ID
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        LOGGER.log(Level.INFO, "GET request received for product with ID: {0}", id);
        return productService.getProductById(id);
    }

    // Endpoint to search for products
    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam(value = "name", required = false) String name,
                                        @RequestParam(value = "code", required = false) UUID code,
                                        @RequestParam(value = "brand", required = false) String brand,
                                        @RequestParam(value = "price",required = false) Long price) {
        LOGGER.log(Level.INFO, "GET request received to search for products");
        return productService.searchProducts(name, code, brand, price);
    }


    // Endpoint to get a specific product by Name
    @GetMapping("name/{name}")
    public List<Product> getProductByName(@PathVariable String name) {
        LOGGER.log(Level.INFO, "GET request received for product with name: {0}", name);
        return productService.getProductByName(name);
    }

    // Endpoint to update an existing product
    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id, @Valid @RequestBody Product productData, BindingResult bindingResult) {
        LOGGER.log(Level.INFO, "PUT request received to update product with ID: {0}", id);
        return productService.updateProduct(id, productData, bindingResult);
    }

    // Endpoint to delete a product by ID
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        LOGGER.log(Level.INFO, "DELETE request received to delete product with ID: {0}", id);
        productService.deleteProduct(id);
    }

    // Endpoint to check product serviceability and get expected delivery time by product ID and pincode
    @GetMapping("/serviceability")
    public Map<String, Object> checkServiceabilityAndDeliveryTime(@RequestParam Long productId, @RequestParam String pincode) {
        LOGGER.log(Level.INFO, "Check Serviceability request received for product with ID: {0}",productId);
        return productService.checkServiceabilityAndDeliveryTime(productId, pincode);
    }

}
