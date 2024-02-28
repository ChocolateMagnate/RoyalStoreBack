package com.royal.orders;

import com.royal.FixtureInitializer;
import com.royal.orders.domain.Order;
import com.royal.orders.repository.OrderRepository;
import com.royal.products.domain.ElectronicProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.ArrayList;

public class OrderFixtureManager extends FixtureInitializer implements ApplicationRunner, AutoCloseable {
    private final OrderRepository orderRepository;

    public OrderFixtureManager(@Autowired OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ArrayList<ElectronicProduct> laptops = loadObjectsFromFixture("fixtures/laptops.yaml", ElectronicProduct.class);
        ArrayList<Order> orders = loadObjectsFromFixture("fixtures/orders.yaml", Order.class);
        for (int index = 0; index < orders.size(); ++index) orders.get(index).setPurchasedProduct(laptops.get(index));
        this.orderRepository.saveAll(orders);
    }

    @Override
    public void close() throws Exception {
        this.orderRepository.deleteAll();
    }
}
