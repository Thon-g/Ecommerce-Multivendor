package com.abs.app.domain.event;

import com.abs.app.domain.entity.Order;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OrderDeliveredEvent extends ApplicationEvent {
    private final Order order;

    public OrderDeliveredEvent(Object source, Order order) {
        super(source);
        this.order = order;
    }
}
