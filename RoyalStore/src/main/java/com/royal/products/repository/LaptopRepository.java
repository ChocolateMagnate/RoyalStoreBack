package com.royal.repositories;

import com.royal.models.products.Laptop;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

public interface LaptopRepository extends MongoRepository<Laptop, String>, QueryByExampleExecutor<Laptop> {
}
