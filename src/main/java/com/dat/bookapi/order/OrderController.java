package com.dat.bookapi.order;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import java.security.Principal;

@RestController
public class OrderController{
    private final OrderService orderService;

    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    @PostMapping("/orders")
    public Order placeOrder(@Valid @RequestBody OrderRequest request, Principal principal){
        return orderService.placeOrder(request, principal.getName());
    }
}
