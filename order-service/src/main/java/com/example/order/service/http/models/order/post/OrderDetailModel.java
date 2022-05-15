package com.example.order.service.http.models.order.post;

import com.example.order.api.domain.model.order.ItemAndNr;
import com.example.order.api.domain.model.order.ItemId;
import com.example.order.api.domain.model.order.ItemNr;
import com.example.order.api.domain.model.order.OrderDetail;

import java.util.List;

public record OrderDetailModel(List<OrderItemModel> items) {
    public OrderDetail toOrderDetail() {
        var itemAndNrList = items.stream().map(this::toItemAndNr).toList();

        return new OrderDetail(
                itemAndNrList
        );
    }

    private ItemAndNr toItemAndNr(OrderItemModel item) {
        return new ItemAndNr(
                new ItemId(item.itemId()),
                new ItemNr(item.amount())
        );
    }
}

