package com.example.order.service.application.interactors.order.cancelduetodiscontinuation;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Scheduler;
import akka.actor.typed.javadsl.AskPattern;
import com.example.order.api.adaptor.aggregate.order.OrderAggregateProtocol;
import com.example.order.api.domain.model.order.AccountId;
import com.example.order.api.domain.model.order.Order;
import com.example.order.api.domain.model.order.OrderDetail;
import com.example.order.api.domain.model.order.OrderId;
import com.example.order.service.application.interactors.order.create.OrderCreateException;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class OrderCancelDueToDiscontinuationInteractor {
    private final Scheduler scheduler;
    private final ActorRef<OrderAggregateProtocol.CommandRequest> orderAggregateRef;
    private final Duration askTimeout;

    public OrderCancelDueToDiscontinuationInteractor(Scheduler scheduler, ActorRef<OrderAggregateProtocol.CommandRequest> orderAggregateRef, Duration askTimeout) {
        this.scheduler = scheduler;
        this.orderAggregateRef = orderAggregateRef;
        this.askTimeout = askTimeout;
    }

    public CompletionStage<OrderId> execute(OrderId orderId) {
        return AskPattern.ask(
                orderAggregateRef,
                (ActorRef<OrderAggregateProtocol.CancelOrderDueToDiscontinuationReply> replyTo) -> new OrderAggregateProtocol.CancelOrderDueToDiscontinuation(orderId, replyTo),
                askTimeout,
                scheduler
        ).thenCompose(result -> {
            if (result instanceof OrderAggregateProtocol.CancelOrderDueToDiscontinuationSucceeded succeeded) {
                return CompletableFuture.completedStage(succeeded.orderId());
            } else if (result instanceof OrderAggregateProtocol.CancelOrderDueToDiscontinuationFailed failed) {
                return CompletableFuture.failedFuture(new OrderCreateException(failed.error().message()));
            }

            throw new IllegalStateException("Unexpected value: " + result);
        });
    }
}
