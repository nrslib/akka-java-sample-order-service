package com.example.order.service.http.controllers;

import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import com.example.order.api.domain.model.order.AccountId;
import com.example.order.api.domain.model.order.OrderId;
import com.example.order.service.application.interactors.order.cancel.OrderCancelInteractor;
import com.example.order.service.application.interactors.order.cancelduetodiscontinuation.OrderCancelDueToDiscontinuationInteractor;
import com.example.order.service.application.interactors.order.create.OrderCreateInteractor;
import com.example.order.service.application.interactors.order.makeshipped.OrderMakeShippedInteractor;
import com.example.order.service.http.models.order.cancel.OrderCancelRequestModel;
import com.example.order.service.http.models.order.cancel.OrderCancelResponseModel;
import com.example.order.service.http.models.order.canceldiscontinuation.OrderCancelDiscontinuationResponseModel;
import com.example.order.service.http.models.order.makeshipped.OrderMakeShippedResponseModel;
import com.example.order.service.http.models.order.post.OrderPostRequestModel;
import com.example.order.service.http.models.order.post.OrderPostResponseModel;

import java.util.UUID;

import static akka.http.javadsl.server.PathMatchers.segment;

public class OrderController extends AllDirectives {
    private final OrderCreateInteractor orderCreateUseCase;
    private final OrderMakeShippedInteractor orderMakeShippedInteractor;
    private final OrderCancelInteractor orderCancelInteractor;
    private final OrderCancelDueToDiscontinuationInteractor orderCancelDueToDiscontinuationInteractor;

    public OrderController(OrderCreateInteractor orderCreateUseCase,
                           OrderMakeShippedInteractor orderMakeShippedInteractor,
                           OrderCancelInteractor orderCancelInteractor,
                           OrderCancelDueToDiscontinuationInteractor orderCancelDueToDiscontinuationInteractor) {
        this.orderCreateUseCase = orderCreateUseCase;
        this.orderMakeShippedInteractor = orderMakeShippedInteractor;
        this.orderCancelInteractor = orderCancelInteractor;
        this.orderCancelDueToDiscontinuationInteractor = orderCancelDueToDiscontinuationInteractor;
    }

    Route toRoute() {
        return pathPrefix("api", () ->
                concat(
                        get(),
                        post(),
                        makeShipped(),
                        cancel(),
                        cancelDueToDiscontinuation()
                ));
    }

    /**
     * GET /api/orders
     * TODO
     */
    Route get() {
        return path("orders", () ->
                get(() -> {
                            return completeOK("ok", Jackson.marshaller());
                        }
                ));
    }

    /**
     * POST /api/orders/{order-id}
     */
    Route post() {
        return path("orders", () ->
                post(() ->
                        entity(Jackson.unmarshaller(OrderPostRequestModel.class), request -> {
                            var accountId = new AccountId(request.accountId());
                            var detail = request.detail().toOrderDetail();
                            var result = orderCreateUseCase.execute(accountId, detail);

                            return onSuccess(result, it ->
                                    complete(StatusCodes.CREATED, new OrderPostResponseModel(it.asString()), Jackson.marshaller()));
                        })
                ));
    }

    /**
     * POST /api/orders/{order-id}/makeShipped
     */
    Route makeShipped() {
        return path(segment("orders").slash(segment().slash("make-shipped")), rawOrderId ->
                post(() -> {
                            var orderId = new OrderId(UUID.fromString(rawOrderId));
                            var result = orderMakeShippedInteractor.execute(orderId);

                            return onSuccess(result, it ->
                                    completeOK(new OrderMakeShippedResponseModel(it.asString()), Jackson.marshaller()));
                        }
                ));
    }

    /**
     * POST /api/orders/{order-id}/cancel
     */
    Route cancel() {
        return path(segment("orders").slash(segment().slash("cancel")), rawOrderId ->
                post(() ->
                        entity(Jackson.unmarshaller(OrderCancelRequestModel.class), request -> {
                            var orderId = new OrderId(UUID.fromString(rawOrderId));
                            var accountId = new AccountId(request.accountId());
                            var result = orderCancelInteractor.execute(orderId, accountId);

                            return onSuccess(result, it ->
                                    completeOK(new OrderCancelResponseModel(it.asString()), Jackson.marshaller()));
                        })
                ));
    }

    /**
     * POST /api/orders/{order-id}/cancel-discontinuation
     */
    Route cancelDueToDiscontinuation() {
        return path(segment("orders").slash(segment().slash("cancel-discontinuation")), rawOrderId ->
                post(() -> {
                            var orderId = new OrderId(UUID.fromString(rawOrderId));
                            var result = orderCancelDueToDiscontinuationInteractor.execute(orderId);

                            return onSuccess(result, it ->
                                    completeOK(new OrderCancelDiscontinuationResponseModel(it.asString()), Jackson.marshaller())
                            );
                        }
                ));
    }
}
