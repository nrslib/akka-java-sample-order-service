package com.example.order.service.application.interactors.order.makeshipped;

import com.example.order.service.application.interactors.order.OrderException;

public class OrderMakeShippedException extends OrderException {
    public OrderMakeShippedException(String message) {
        super(message);
    }
}
