package com.example.order.service.http.controllers;

import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import com.example.order.service.http.middleware.exception.ExceptionHandlers;

public class Routes extends AllDirectives {
    private final OrderController orderController;

    public Routes(OrderController orderController) {
        this.orderController = orderController;
    }

    public Route getRoutes() {
        return handleExceptions(ExceptionHandlers.exceptionHandler(), () ->
                concat(
                        orderController.toRoute()
                ));
    }
}
