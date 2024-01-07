package com.royal.repositories;

import com.royal.models.products.Smartphone;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SmartphoneRepository extends MongoRepository<Smartphone, String> {

}
