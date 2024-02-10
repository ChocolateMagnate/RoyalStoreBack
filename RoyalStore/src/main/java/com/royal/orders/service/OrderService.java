package com.royal.orders.service;

import com.royal.errors.HttpException;
import com.royal.orders.domain.Order;
import com.royal.orders.domain.Page;
import com.royal.orders.repository.OrderRepository;
import com.royal.products.domain.ElectronicProduct;
import com.royal.products.repository.ElectronicProductRepository;
import com.royal.users.domain.User;
import com.royal.users.repository.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class OrderService {
    private final UserRepository userRepository;
    private final ElectronicProductRepository electronicProductRepository;
    private final OrderRepository orderRepository;

    public static int ElementsInPage = 100;

    public OrderService(@Autowired OrderRepository orderRepository,
                        @Autowired UserRepository userRepository,
                        @Autowired ElectronicProductRepository electronicProductRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.electronicProductRepository = electronicProductRepository;
    }

    public Set<Order> getAllOrdersOfUser(String email) throws HttpException {
        Set<Order> orders = this.userRepository.findByEmail(email).orElseThrow(
                () -> new HttpException(HttpStatus.NOT_FOUND, "No user found under the email " + email))
                .getOrders();
        if (orders == null) return new HashSet<>();
        else return orders;
    }

    public Page<Order> getPagedOrders(String userId, long requestedPageIndex) throws HttpException {
        Set<Order> allOrders = getOrdersOrThrow(userId);
        if (allOrders.size() <= ElementsInPage) return new Page<>(allOrders.size(), 0, allOrders);
        long totalPageAmount = allOrders.size() / ElementsInPage;
        if (totalPageAmount < requestedPageIndex) throw new HttpException(HttpStatus.NOT_FOUND,
                "There are only " + totalPageAmount + " pages of orders, while page #"
                        + requestedPageIndex + " was requested.");

        int beginning = (int) (requestedPageIndex * ElementsInPage);
        int ending = beginning + ElementsInPage;
        List<Order> selectedOrders = new ArrayList<>(allOrders).subList(beginning, ending);
        return new Page<>(totalPageAmount, requestedPageIndex, new HashSet<>(selectedOrders));
    }

    public Order getOrderById(String orderId) throws HttpException {
        return this.orderRepository.findById(orderId).orElseThrow(() ->
                new HttpException(HttpStatus.NOT_FOUND, "No order under the id " + orderId));
    }

    public String makeOrder(String email, String productId) throws HttpException {
        // Normally you would want to use some banking API to request a purchase, but since this is
        // a demonstration project, payment integration is not topical (at the moment) and we make
        // orders simply by adding them to the order collection.
        var newOrder = new Order();
        newOrder.setIssuedAt(Instant.now());
        newOrder.setLastModifiedAt(newOrder.getIssuedAt());
        newOrder.setPurchasedProduct(getProductOrThrow(productId));
        Set<Order> orders = getOrdersOrThrow(email);
        boolean sameOrderExists = orders.stream().anyMatch(order ->
                Objects.equals(order.getPurchasedProduct().getId(), productId));
        if (sameOrderExists) return productId;
        Order savedOrder = this.orderRepository.save(newOrder);
        addOrderIdToUser(email, savedOrder);
        return savedOrder.getId();
    }

    public void cancelOrder(String userId, String orderId) throws HttpException {
        Set<Order> orders = getOrdersOrThrow(userId);
        Optional<Order> orderToCancel = this.orderRepository.findById(orderId);
        if (orderToCancel.isPresent()) {
            Order cancelledOrder = orderToCancel.get().cancel();
            this.orderRepository.save(cancelledOrder);
        }
    }

    public void progressOrderStatus(String orderId) throws HttpException {
        Order updatedOrder = getOrderById(orderId);
        Order progressedOrder = updatedOrder.progress();
        this.orderRepository.save(progressedOrder);
    }

    private void addOrderIdToUser(String email, Order order) throws HttpException {
        Optional<User> targetUser = this.userRepository.findByEmail(email);
        if (targetUser.isEmpty()) throw new HttpException
                (HttpStatus.NOT_FOUND, "No user under the email " + email);
        User user = targetUser.get();
        user.getOrders().add(order);
    }

    private @NotNull Set<Order> getOrdersOrThrow(String userId) throws HttpException {
        Set<Order> orders = this.userRepository.findById(userId).orElseThrow(
                () -> new HttpException(HttpStatus.NOT_FOUND, "No user under the id " + userId))
                .getOrders();
        if (orders == null) return new HashSet<>();
        else return orders;
    }

    private Order getOrderOrThrow(String orderId) throws HttpException {
        return this.orderRepository.findById(orderId).orElseThrow(
                () -> new HttpException(HttpStatus.NOT_FOUND, "No order under the id " + orderId));
    }

    private ElectronicProduct getProductOrThrow(String productId) throws HttpException {
        return this.electronicProductRepository.findById(productId).orElseThrow(
                () -> new HttpException(HttpStatus.NOT_FOUND, "No product under the id " + productId));
    }
}
