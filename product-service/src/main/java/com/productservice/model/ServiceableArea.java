package com.productservice.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceableArea {

    @Id
    private String pincode;

    private String deliveryTime;

    // Constructors, getters, setters...
}
