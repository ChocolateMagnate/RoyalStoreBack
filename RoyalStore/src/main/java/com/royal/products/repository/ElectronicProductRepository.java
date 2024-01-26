package com.royal.products.repository;

import com.royal.products.domain.ElectronicProduct;
import com.royal.products.domain.ProductCategory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ElectronicProductRepository extends MongoRepository<ElectronicProduct, String> {
    List<ElectronicProduct> findAllByCategory(ProductCategory category);

}
