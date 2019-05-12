package com.sy.sa.common;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

/**
 * OrderHandler: 消息消费者，处理事件
 * 
 * @data 2019年5月8日 下午5:39:21
 * @author ztq
 **/
public class OrderHandler implements EventHandler<Order>, WorkHandler<Order> {
	private String consumerId;

	public OrderHandler(String consumerId) {
		this.consumerId = consumerId;
	}
	// step_3:定义事件处理的具体实现:
	// EventHandler模式
	@Override
	public void onEvent(Order order, long sequence, boolean endOfBatch) throws Exception {
		System.out.println("OrderHandler 消费者编号" + this.consumerId + "，消费的资源编号：" + order.getId());
	}

	// WorkHandler模式
	@Override
	public void onEvent(Order order) throws Exception {
		System.out.println("OrderHandler 消费者编号" + this.consumerId + "，消费的资源编号：" + order.getId());
	}

}
