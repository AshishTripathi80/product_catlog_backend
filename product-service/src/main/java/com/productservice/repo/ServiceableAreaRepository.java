package com.productservice.repo;

import com.productservice.model.ServiceableArea;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceableAreaRepository extends JpaRepository<ServiceableArea, String> {

    ServiceableArea findByPincode(String pincode);
}
