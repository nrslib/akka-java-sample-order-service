package com.example.order.service.http.middleware.exception;

import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.ExceptionHandler;
import com.example.supportstack.domain.DomainException;

import static akka.http.javadsl.server.Directives.complete;

public class ExceptionHandlers {
    public static ExceptionHandler exceptionHandler() {
        return ExceptionHandler.newBuilder()
                .match(DomainException.class, ex ->
                        complete(StatusCodes.BAD_REQUEST, ex.getMessage())
                )
                .build();
    }
}
