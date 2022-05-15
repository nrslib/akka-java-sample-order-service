package com.example.order.service.application.interactors.order.makeshipped;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Scheduler;
import akka.actor.typed.javadsl.AskPattern;
import com.example.order.api.adaptor.aggregate.order.OrderAggregateProtocol;
import com.example.order.api.domain.model.order.OrderId;
import com.example.order.service.application.interactors.order.create.OrderCreateException;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class OrderMakeShippedInteractor {
    private final Scheduler scheduler;
    private final ActorRef<OrderAggregateProtocol.CommandRequest> orderAggregateRef;
    private final Duration askTimeout;

    public OrderMakeShippedInteractor(Scheduler scheduler, ActorRef<OrderAggregateProtocol.CommandRequest> orderAggregateRef, Duration askTimeout) {
        this.scheduler = scheduler;
        this.orderAggregateRef = orderAggregateRef;
        this.askTimeout = askTimeout;
    }

    public CompletionStage<OrderId> execute(OrderId orderId) {
        return AskPattern.ask(
                orderAggregateRef,
                (ActorRef<OrderAggregateProtocol.MakeOrderReply> replyTo) -> new OrderAggregateProtocol.MakeOrderShipped(orderId, replyTo),
                askTimeout,
                scheduler
        ).thenCompose(result -> {
            if (result instanceof OrderAggregateProtocol.MakeOrderShippedSucceeded succeeded) {
                return CompletableFuture.completedStage(succeeded.orderId());
            } else if (result instanceof OrderAggregateProtocol.MakeOrderShippedFailed failed) {
                return CompletableFuture.failedFuture(new OrderCreateException(failed.error().message()));
            }

            throw new IllegalStateException("Unexpected value: " + result);
        });
    }
}
