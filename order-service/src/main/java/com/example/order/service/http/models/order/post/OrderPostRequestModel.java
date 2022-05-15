package com.example.order.service.http.models.order.post;

public record OrderPostRequestModel(String accountId, OrderDetailModel detail) {
}
