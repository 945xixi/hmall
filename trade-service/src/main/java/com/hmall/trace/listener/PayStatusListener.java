package com.hmall.trace.listener;

import com.hmall.trace.domain.po.Order;
import com.hmall.trace.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PayStatusListener {

    private final IOrderService orderService;

    // TODO：1mq--消费者
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "trade.pay.success.queue", durable = "true"),
            exchange = @Exchange(name = "pay.direct"),
            key = "pay.success"
    ))
    public void listenPaySuccess(Long orderId){
        // TODO：1mq--幂等处理
        // 查询订单，获取订单状态
        Order order = orderService.getById(orderId);
        if (order == null || order.getStatus() != 1){
            // 只有状态为未支付时，才能修改为已支付，所以
            // ！=1的都不处理，直接返回，此时就算重复执行也不影响
            return;
        }
        orderService.markOrderPaySuccess(orderId);
    }
}
