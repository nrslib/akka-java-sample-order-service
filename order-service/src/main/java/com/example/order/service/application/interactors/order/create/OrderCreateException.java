package com.example.order.service.application.interactors.order.create;

import com.example.order.service.application.interactors.order.OrderException;

public class OrderCreateException extends OrderException {
    public OrderCreateException(String message) {
        super(message);
    }
}
