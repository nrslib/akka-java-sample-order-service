package com.example.order.service.application.interactors.order.cancel;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Scheduler;
import akka.actor.typed.javadsl.AskPattern;
import com.example.order.api.adaptor.aggregate.order.OrderAggregateProtocol;
import com.example.order.api.domain.model.order.AccountId;
import com.example.order.api.domain.model.order.OrderId;
import com.example.order.service.application.interactors.order.create.OrderCreateException;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class OrderCancelInteractor {
    private final Scheduler scheduler;
    private final ActorRef<OrderAggregateProtocol.CommandRequest> orderAggregateRef;
    private final Duration askTimeout;

    public OrderCancelInteractor(Scheduler scheduler, ActorRef<OrderAggregateProtocol.CommandRequest> orderAggregateRef, Duration askTimeout) {
        this.scheduler = scheduler;
        this.orderAggregateRef = orderAggregateRef;
        this.askTimeout = askTimeout;
    }

    public CompletionStage<OrderId> execute(OrderId orderId, AccountId accountId) {
        return AskPattern.ask(
                orderAggregateRef,
                (ActorRef<OrderAggregateProtocol.CancelOrderReply> replyTo) -> new OrderAggregateProtocol.CancelOrder(orderId, accountId, replyTo),
                askTimeout,
                scheduler
        ).thenCompose(result -> {
            if (result instanceof OrderAggregateProtocol.CancelOrderSucceeded succeeded) {
                return CompletableFuture.completedStage(succeeded.orderId());
            } else if (result instanceof OrderAggregateProtocol.CancelOrderFailed failed) {
                return CompletableFuture.failedFuture(new OrderCreateException(failed.error().message()));
            }

            throw new IllegalStateException("Unexpected value: " + result);
        });
    }
}
