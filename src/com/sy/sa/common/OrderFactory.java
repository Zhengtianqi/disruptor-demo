package com.sy.sa.common;

import com.lmax.disruptor.EventFactory;

/**
 * OrderFactory: 事件工厂
 * 
 * @data 2019年5月8日 下午5:36:11
 * @author ztq
 **/
public class OrderFactory implements EventFactory<Order> {
	// step_2: 定义事件工厂
	@Override
	public Order newInstance() {
		return new Order();
	}
}