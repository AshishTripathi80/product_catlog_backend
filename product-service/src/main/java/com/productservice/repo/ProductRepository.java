package com.productservice.repo;

import com.productservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByName(String name);

    List<Product> findAllByCategory(String category);

    List<Product> findAllByBrand(String brand);

    List<Product> findAllByCode(UUID code);

    List<Product> findAllByPrice(Long price);
}
