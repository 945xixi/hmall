package com.hmall.trace.listener;

import com.hmall.api.client.PayClient;
import com.hmall.api.dto.PayOrderDTO;
import com.hmall.trace.constants.MQConstants;
import com.hmall.trace.domain.po.Order;
import com.hmall.trace.service.IOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderDelayMessageListener {

    private final IOrderService orderService;

    private final PayClient payClient;

    /**
     * 取消超时未支付订单
     * @param orderId
     */
    // TODO；2mq（取消超时未支付订单）--消费者
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MQConstants.DELAY_ORDER_QUEUE_NAME),
            exchange = @Exchange(name = MQConstants.DELAY_EXCHANGE_NAME, delayed = "true"),
            key = MQConstants.DELAY_ORDER_KEY
    ))
    public void listenOrderDelayMessage(Long orderId){
        // 查询订单
        Order order = orderService.getById(orderId);
        // 获取订单状态，如果不是未付款，直接返回，不用取消
        if (order == null || order.getStatus() != 1){
            return;
        }
        // 查看支付订单流水PayOrder，看是否已支付
        PayOrderDTO pay = payClient.queryPayOrderByBizOrderNo(orderId);
        // 是已支付，更新订单状态为已支付成功（双重保障）
        if (pay == null || pay.getStatus() == 3){
            orderService.markOrderPaySuccess(orderId);
        }else {
            // 不是已支付，此时已经超时，取消订单，将订单状态改为取消，恢复库存
            orderService.cancelOrder(orderId);
            log.info("取消超时订单:{}", orderId);
        }

    }
}
