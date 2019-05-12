package com.sy.sa.common;

import com.lmax.disruptor.RingBuffer;

/**
 * Producer: 生产者，获取可用的空间
 * @data 2019年5月8日 下午5:48:24
 * @author ztq
 **/
public class Producer {
	private final RingBuffer<Order> ringBuffer;

	public Producer(RingBuffer<Order> ringBuffer) {
		this.ringBuffer = ringBuffer;
	}

	public void onData(String data) {
		long sequence = ringBuffer.next();
		try {
			Order order = ringBuffer.get(sequence);
			order.setId(data);
		} finally {
			ringBuffer.publish(sequence);
		}
	}
}
