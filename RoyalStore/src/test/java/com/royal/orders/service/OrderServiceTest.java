package com.royal.orders.service;

import com.royal.orders.domain.Page;
import com.royal.orders.domain.Order;
import com.royal.orders.repository.OrderRepository;
import com.royal.products.repository.ElectronicProductRepository;
import com.royal.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(properties = "spring.config.location=classpath:application.yaml")
class OrderServiceTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ElectronicProductRepository electronicProductRepository;
    @Autowired
    private OrderRepository orderRepository;
    private OrderService orderService;

    @BeforeAll
    public void setUp() {
        // All ordering logic requires a valid authenticated user, hence we manually authenticate
        // a mock user in order to be able to test this service separately from the controller.
        String principal = "65b66e9457030f82dd517e73";
        String password = "NfzHKbCE5ccezGyNratWBvEDg4nqNzZA";
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String getMockedAuthenticatedUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
    }

    @Test
    public void testGetAllOrdersOfUser() {
        assertDoesNotThrow(() -> {
            Set<Order> allOrders = this.orderService.getAllOrdersOfUser(getMockedAuthenticatedUserId());
            if (!allOrders.isEmpty()) for (Order order : allOrders)
                assertTrue(this.orderRepository.existsById(order.getId()));
        });
    }

    @Test
    public void testGetPagedOrders() {
        String id = getMockedAuthenticatedUserId();
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
        String id = getMockedAuthenticatedUserId();

    }

    @Test
    public void testMakeOrder() {
    }

    @Test
    public void testCancelOrder() {
    }

    @Test
    public void testProgressOrderStatus() {
    }
}