package com.royal.orders.controller;

import com.royal.errors.HttpException;
import com.royal.orders.domain.Order;
import com.royal.orders.domain.Page;
import com.royal.orders.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
public class OrderController {
    private final OrderService orderService;

    public OrderController(@Autowired OrderService service) {
        this.orderService = service;
    }

    @GetMapping("/orders/get-all-orders")
    public Set<Order> getAllOrders(@RequestParam("userId") String userId) throws HttpException {
        return this.orderService.getAllOrdersOfUser(userId);
    }

    @GetMapping("/orders/get-paged-orders")
    public Page<Order> getPagedOrders(@RequestParam("page") long requestedPageIndex,
                                      @RequestParam("userId") String userId) throws HttpException {
        return this.orderService.getPagedOrders(userId, requestedPageIndex);
    }

    @GetMapping("/orders/get-order")
    public Order getOrder(@RequestParam("id") String orderId) throws HttpException {
        return this.orderService.getOrderById(orderId);
    }

    @PostMapping("/orders/make-order")
    public String addToOrders(@RequestParam("userId") String userId,
                              @RequestParam("productId") String productId) throws HttpException {
        return this.orderService.makeOrder(userId, productId);
    }

    @PostMapping("/orders/cancel-order")
    public void cancelOrder(@RequestParam("userId") String userId,
                            @RequestParam("orderId") String orderId) throws HttpException {
        this.orderService.cancelOrder(userId, orderId);
    }

    @PutMapping("/orders/progress-order-status")
    public void progressOrderStatus(@RequestParam("id") String orderId) throws HttpException {
        this.orderService.progressOrderStatus(orderId);
    }

}
