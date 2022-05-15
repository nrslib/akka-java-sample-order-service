package com.example.order.service.application.interactors.order;

import com.example.supportstack.domain.DomainException;

public abstract class OrderException extends DomainException {
    protected OrderException(String message) {
        super(message);
    }
}
