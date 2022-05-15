package com.example.order.service.application.interactors.order.create;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Scheduler;
import akka.actor.typed.javadsl.AskPattern;
import com.example.order.api.adaptor.aggregate.order.OrderAggregateProtocol;
import com.example.order.api.domain.model.order.AccountId;
import com.example.order.api.domain.model.order.OrderDetail;
import com.example.order.api.domain.model.order.OrderId;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class OrderCreateInteractor {
    private final Scheduler scheduler;
    private final ActorRef<OrderAggregateProtocol.CommandRequest> orderAggregateRef;
    private final Duration askTimeout;

    public OrderCreateInteractor(Scheduler scheduler, ActorRef<OrderAggregateProtocol.CommandRequest> orderAggregateRef, Duration askTimeout) {
        this.scheduler = scheduler;
        this.orderAggregateRef = orderAggregateRef;
        this.askTimeout = askTimeout;
    }

    public CompletionStage<OrderId> execute(AccountId accountId, OrderDetail detail) {
        var orderId = new OrderId(UUID.randomUUID());

        return AskPattern.ask(
                orderAggregateRef,
                (ActorRef<OrderAggregateProtocol.CreateOrderReply> replyTo) -> new OrderAggregateProtocol.CreateOrderRequest(orderId, accountId, detail, replyTo),
                askTimeout,
                scheduler
        ).thenCompose(result -> {
            if (result instanceof OrderAggregateProtocol.CreateOrderSucceeded succeeded) {
                return CompletableFuture.completedStage(succeeded.orderId());
            } else if (result instanceof OrderAggregateProtocol.CreateOrderFailed failed) {
                return CompletableFuture.failedFuture(new OrderCreateException(failed.error().message()));
            }

            throw new IllegalStateException("Unexpected value: " + result);
        });
    }
}
