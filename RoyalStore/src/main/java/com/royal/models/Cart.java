package com.royal.models;

import com.royal.models.products.ElectronicProduct;
import com.royal.models.products.Laptop;
import com.royal.models.products.Smartphone;
import com.royal.repositories.LaptopRepository;
import com.royal.repositories.SmartphoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;

import javax.management.AttributeNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Cart extends ArrayList<CartPair> {
    @Autowired
    private LaptopRepository laptopRepository;
    @Autowired
    private SmartphoneRepository smartphoneRepository;

    public boolean contains(String productId) {
        for (CartPair pair : this.stream().toList())
            if (Objects.equals(pair.getProductId(), productId)) return true;
        return false;
    }

    public ArrayList<ElectronicProduct> getAllElectronicProducts() throws AttributeNotFoundException {
        ArrayList<ElectronicProduct> electronicProducts = new ArrayList<>();
        List<String> productReferences = this.stream().map(CartPair::getProductId).toList();
        for (String productId : productReferences) {
            ElectronicProduct product;
            Optional<Laptop> optionalLaptop = laptopRepository.findById(productId);
            if (optionalLaptop.isEmpty()) {
                Optional<Smartphone> optionalSmartphone = smartphoneRepository.findById(productId);
                if (optionalSmartphone.isEmpty())
                    throw new AttributeNotFoundException("Product under ID " + productId + " does not exist.");
                product = optionalSmartphone.get();
            } else product = optionalLaptop.get();
            electronicProducts.add(product);
        }
        return electronicProducts;
    }

    public int whereProductIdEquals(String productId) throws AttributeNotFoundException {
        for (int index = 0; index < size(); ++index) {
            CartPair pair = this.get(index);
            if (Objects.equals(pair.getProductId(), productId)) return index;
        }
        throw new AttributeNotFoundException("No product under the ID " + productId);
    }
}


