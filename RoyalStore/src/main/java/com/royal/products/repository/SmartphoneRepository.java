package com.royal.products.repository;

import com.royal.products.domain.Smartphone;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SmartphoneRepository extends MongoRepository<Smartphone, String> {

}
