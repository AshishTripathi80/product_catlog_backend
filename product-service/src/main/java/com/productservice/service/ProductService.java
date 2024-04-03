package com.productservice.service;

import com.productservice.enums.Constants;
import com.productservice.exception.InvalidProductDataException;
import com.productservice.exception.ProductNotFoundException;
import com.productservice.model.MultipleProduct;
import com.productservice.model.Product;
import com.productservice.model.ServiceableArea;
import com.productservice.repo.ProductRepository;
import com.productservice.repo.ServiceableAreaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ServiceableAreaRepository serviceableAreaRepository;

    /**
     * Retrieve all products.
     *
     * @return The List of products.
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Create a new product.
     *
     * @param product       The product to be created.
     * @param bindingResult The BindingResult object for validation.
     * @return The created product.
     */
    public Product createProduct(Product product, BindingResult bindingResult) {
        // Check for validation errors
        validationError(bindingResult);
        product.setCode(UUID.randomUUID());
        return productRepository.save(product);
    }

    /**
     * Handle validation errors.
     *
     * @param bindingResult The BindingResult object containing validation errors.
     * @throws InvalidProductDataException if validation errors are present.
     */
    public void validationError(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            List<String> errors = new ArrayList<>();

            // Convert each field error to a string representation
            for (FieldError fieldError : fieldErrors) {
                errors.add(fieldError.getField() + ": " + fieldError.getDefaultMessage());
            }

            // Throw an exception with the validation errors
            throw new InvalidProductDataException("Validation Failed!", LocalDateTime.now(), errors);
        }
    }

    /**
     * Retrieve a product by ID.
     *
     * @param id The ID of the product.
     * @return The retrieved product.
     * @throws ProductNotFoundException if the product is not found.
     */
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(Constants.ERROR_PRODUCT_NOT_FOUND.getMessage() + id));
    }

    /**
     * Retrieve products by name.
     *
     * @param name The name of the products.
     * @return The list of products with the given name.
     * @throws ProductNotFoundException if no products are found with the given name.
     */
    public List<Product> getProductByName(String name) {
        List<Product> products = productRepository.findAllByName(name);
        if (products.isEmpty()) {
            throw new ProductNotFoundException(Constants.ERROR_PRODUCT_NOT_FOUND.getMessage() + name);
        }
        return products;
    }

    /**
     * Retrieve products by category.
     *
     * @param code The category of the products.
     * @return The list of products with the given category.
     * @throws ProductNotFoundException if no products are found with the given category.
     */
    public List<Product> getProductByCode(UUID code) {
        List<Product> products = productRepository.findAllByCode(code);
        if (products.isEmpty()) {
            throw new ProductNotFoundException(Constants.ERROR_PRODUCT_NOT_FOUND.getMessage() + code);
        }
        return products;
    }

    /**
     * Retrieve products by brand.
     *
     * @param brand The brand of the products.
     * @return The list of products with the given brand.
     * @throws ProductNotFoundException if no products are found with the given brand.
     */
    public List<Product> getProductByBrand(String brand) {
        List<Product> products = productRepository.findAllByBrand(brand);
        if (products.isEmpty()) {
            throw new ProductNotFoundException(Constants.ERROR_PRODUCT_NOT_FOUND.getMessage() + brand);
        }
        return products;
    }

    /**
     * Update a product.
     *
     * @param id            The ID of the product to be updated.
     * @param productData   The updated product data.
     * @param bindingResult The BindingResult object for validation.
     * @return The updated product.
     */
    public Product updateProduct(Long id, Product productData, BindingResult bindingResult) {
        // Check for validation errors
        validationError(bindingResult);

        Product product = getProductById(id);

        product.setName(productData.getName());
        product.setDescription(productData.getDescription());
        product.setBrand(productData.getBrand());
        product.setPrice(productData.getPrice());
        product.setCategory(productData.getCategory());
        product.setAvailableQuantity(productData.getAvailableQuantity());
        product.setCode(productData.getCode());

        return productRepository.save(product);
    }

    /**
     * Delete a product by ID.
     *
     * @param id The ID of the product to be deleted.
     */
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }

    /**
     * Create multiple products.
     *
     * @param multipleProduct The object containing multiple products.
     * @return The list of created products.
     */
    public List<Product> createMultipleProducts(MultipleProduct multipleProduct) {
        List<Product> products = multipleProduct.getProducts();
        for (Product product : products) {
            UUID uuid = UUID.randomUUID();
            product.setCode(uuid);
        }


        return productRepository.saveAll(products);
    }

    /**
     * Search for products based on provided parameters.
     *
     * @param name     The name of the products to search for.
     * @param code The category of the products to search for.
     * @param brand    The brand of the products to search for.
     * @return The list of products matching the search criteria.
     */
    public List<Product> searchProducts(String name, UUID code, String brand, Long price) {
        // Perform the search based on the provided parameters
        List<Product> searchResults = new ArrayList<>();

        // Search by name
        if (name != null && !name.equals("")) {
            searchResults.addAll(getProductByName(name));
        }

        // Search by category
        if (code != null) {
            searchResults.addAll(getProductByCode(code));
        }

        // Search by brand
        if (brand != null && !brand.equals("")) {
            searchResults.addAll(getProductByBrand(brand));
        }
        //Search By price
        if (price != null){
            searchResults.addAll(getProductByPrice(price));
        }
        if(brand==null && code==null && name==null && price==null){
            searchResults.addAll(getAllProducts());
        }
        if(Objects.equals(brand, "") || Objects.equals(name, "")){
            searchResults.addAll(getAllProducts());
        }

        return searchResults;
    }

    private Collection<? extends Product> getProductByPrice(Long price) {
        return productRepository.findAllByPrice(price);
    }

    // Method to check product serviceability and get expected delivery time
    public Map<String, Object> checkServiceabilityAndDeliveryTime(Long productId, String pincode) {
        Map<String, Object> response = new HashMap<>();

        // Retrieve the product
        Product product = getProductById(productId);
        if (product == null) {
            response.put("message", "Product not found");
            return response;
        }

        // Check if the product is deliverable to the given pincode (You can implement your own logic here)
        boolean isServiceable = checkServiceability(pincode);
        response.put("isServiceable", isServiceable);

        // Calculate expected delivery time (You can implement your own logic here)
        String deliveryTime = getDeliveryTime(pincode);
        response.put("deliveryTime", deliveryTime+" Days");

        return response;
    }

    // Method to check serviceability based on pincode
    private boolean checkServiceability(String pincode) {
        ServiceableArea serviceableArea = serviceableAreaRepository.findByPincode(pincode);
        return serviceableArea != null;
    }

    // Method to get delivery time based on pincode
    private String getDeliveryTime(String pincode) {
        ServiceableArea serviceableArea = serviceableAreaRepository.findByPincode(pincode);
        return serviceableArea != null ? serviceableArea.getDeliveryTime() : null;
    }
}
