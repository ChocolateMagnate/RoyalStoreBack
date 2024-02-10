package com.royal.orders.service;

import com.royal.errors.HttpException;
import com.royal.orders.domain.OrderStatus;
import com.royal.orders.domain.Page;
import com.royal.orders.domain.Order;
import com.royal.orders.repository.OrderRepository;
import com.royal.products.repository.ElectronicProductRepository;
import com.royal.users.UserFixtureManager;
import com.royal.users.domain.User;
import com.royal.users.repository.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.io.IOException;
import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(properties = "spring.config.location=classpath:application.yaml")
class OrderServiceTest {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ElectronicProductRepository electronicProductRepository;
    @Autowired
    private OrderRepository orderRepository;
    private OrderService orderService;
    private UserFixtureManager fixtures;
    private User customer;

    @BeforeAll
    public void setUp() throws IOException {
        this.fixtures = new UserFixtureManager(userRepository, passwordEncoder);
        this.customer = fixtures.getFixtureUser();
        this.orderService = new OrderService(orderRepository, userRepository, electronicProductRepository);
    }

    private String getCustomerEmail() {
        return this.customer.getEmail();
    }

    @Test
    public void testGetAllOrdersOfUser() {
        assertDoesNotThrow(() -> {
            Set<Order> allOrders = this.orderService.getAllOrdersOfUser(getCustomerEmail());
            if (!allOrders.isEmpty()) for (Order order : allOrders)
                assertTrue(this.orderRepository.existsById(order.getId()));
        });
    }

    @Test
    public void testGetPagedOrders() {
        String id = getCustomerEmail();
        assertDoesNotThrow(() -> {
            Page<Order> firstPage = this.orderService.getPagedOrders(id, 0);
            HashSet<Order> introspection = new HashSet<>(firstPage.getContent());
            assertEquals(firstPage.getIndex(), 0);
            long totalOrders = firstPage.getTotal();
            assertTrue(firstPage.getTotal() <= OrderService.ElementsInPage);
            long remainingPages = firstPage.getTotal() / OrderService.ElementsInPage;
            for (long page = 1; page < remainingPages; ++page) {
                Page<Order> orders = this.orderService.getPagedOrders(id, page);
                introspection.retainAll(orders.getContent());
                assertTrue(introspection.isEmpty());
                assertEquals(orders.getIndex(), page);
                assertEquals(orders.getTotal(), totalOrders);
                assertEquals(orders.getContent().size(), OrderService.ElementsInPage);
            }
        });
    }

    @Test
    public void testGetOrderById() {
        String email = getCustomerEmail();
        assertDoesNotThrow(() -> {
            Set<Order> allOrders = this.orderService.getAllOrdersOfUser(email);
            for (Order order : allOrders) assertTrue(this.orderRepository.existsById(order.getId()));
        });

    }

    @Test
    public void testMakeOrder() {
        String email = getCustomerEmail();
        assertDoesNotThrow(() -> {
            String orderId = this.orderService.makeOrder(email, getExistingProductId());
            assertTrue(this.orderRepository.existsById(orderId));
        });
    }

    @Test
    public void testCancelOrder() {
        String email = getCustomerEmail();
        assertDoesNotThrow(() -> {
            Optional<Order> optionalOrder = requestMakeOrder();
            assertTrue(optionalOrder.isPresent());
            String orderId = optionalOrder.get().getId();
            Instant issuedAt = optionalOrder.get().getLastModifiedAt();

            this.orderService.cancelOrder(email, orderId);
            Optional<Order> cancelledOrder = this.orderRepository.findById(orderId);
            assertTrue(cancelledOrder.isPresent());
            assertSame(cancelledOrder.get().getStatus(), OrderStatus.Cancelled);
            Instant cancelledAt = cancelledOrder.get().getLastModifiedAt();
            assertNotEquals(issuedAt, cancelledAt);
        });
    }

    @Test
    public void testProgressOrderStatus() {
        assertDoesNotThrow(() -> {
            Optional<Order> requestedOrder = requestMakeOrder();
            assertTrue(requestedOrder.isPresent());
            Order pendingOrder = requestedOrder.get();
            String orderId = pendingOrder.getId();
            assertSame(OrderStatus.Pending, pendingOrder.getStatus());
            this.orderService.progressOrderStatus(orderId);

            requestedOrder = this.orderRepository.findById(orderId);
            assertTrue(requestedOrder.isPresent());
            Order confirmedOrder = requestedOrder.get();
            assertSame(OrderStatus.Confirmed, confirmedOrder.getStatus());
        });
    }

    private String getExistingProductId() {
        return this.electronicProductRepository.findAll().get(0).getId();
    }

    private @NotNull Optional<Order> requestMakeOrder() throws HttpException {
        String orderId = this.orderService.makeOrder(getCustomerEmail(), getExistingProductId());
        return this.orderRepository.findById(orderId);
    }
}
