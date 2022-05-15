package com.example.order.api.domain.model.order;

import java.util.List;

public record OrderDetail(List<ItemAndNr> itemAndNrList) {
}
