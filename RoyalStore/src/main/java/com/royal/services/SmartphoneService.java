package com.royal.services;

import com.royal.errors.HttpException;
import com.royal.models.products.Smartphone;
import com.royal.repositories.SmartphoneRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class SmartphoneService {
    @Autowired
    private SmartphoneRepository smartphoneRepository;
    @Autowired
    private MongoTemplate template;

    public List<Smartphone> getAllSmartphonesByParameters(HashMap<String, Object> filter) {
        Criteria criteria = new Criteria();
        for (String key : filter.keySet()) {
            Object value = filter.get(key);
            if (value != null) criteria.and(key).is(value);
        }
        Query customisedVariableParameterQuery = new Query(criteria);
        return template.find(customisedVariableParameterQuery, Smartphone.class);
    }

    public void createSmartphone(Smartphone newSmartphone) throws HttpException {
        if (smartphoneRepository.exists(Example.of(newSmartphone)))
            throw new HttpException(HttpStatus.FOUND, "The same smartphone already exists.");
        smartphoneRepository.save(newSmartphone);
    }

    public void updateSmartphoneById(String id, Smartphone updatedSmartphone) throws HttpException {
        Optional<Smartphone> smartphoneInDatabase = smartphoneRepository.findById(id);
        if (smartphoneInDatabase.isEmpty())
            throw new HttpException(HttpStatus.NOT_FOUND, "Smartphone by ID " + id + " already exists.");
        Smartphone extractedSmartphone = smartphoneInDatabase.get();
        extractedSmartphone.setOs(updatedSmartphone.getOs());
        extractedSmartphone.setBrand(updatedSmartphone.getBrand());
        extractedSmartphone.setPrice(updatedSmartphone.getPrice());
        extractedSmartphone.setMemory(updatedSmartphone.getMemory());
        extractedSmartphone.setMaterial(updatedSmartphone.getMaterial());
        extractedSmartphone.setDiagonal(updatedSmartphone.getDiagonal());
        extractedSmartphone.setProcessor(updatedSmartphone.getProcessor());
        extractedSmartphone.setResolution(updatedSmartphone.getResolution());
        extractedSmartphone.setConnectivities(updatedSmartphone.getConnectivities());
        extractedSmartphone.setBatteryCapacityMAH(updatedSmartphone.getBatteryCapacityMAH());
        smartphoneRepository.save(extractedSmartphone);
    }

    public void deleteSmartphoneById(String id) throws HttpException {
        if (!smartphoneRepository.existsById(id))
            throw new HttpException(HttpStatus.NOT_FOUND, "The smartphone by ID " + id + " doesn't exist.");
        smartphoneRepository.deleteById(id);
    }
}
