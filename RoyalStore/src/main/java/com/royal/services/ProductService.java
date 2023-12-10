package com.royal.services;

import com.royal.repositories.LaptopRepository;
import com.royal.repositories.SmartphoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    @Autowired
    private LaptopRepository laptopRepository;
    @Autowired
    private SmartphoneRepository smartphoneRepository;

        
}
