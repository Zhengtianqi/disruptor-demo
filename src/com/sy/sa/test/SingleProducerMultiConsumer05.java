package com.sy.sa.test;

import java.util.concurrent.Executors;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.sy.sa.common.Order;
import com.sy.sa.common.OrderFactory;
import com.sy.sa.common.OrderHandler;
import com.sy.sa.common.Producer;

/**
 * @data 2019年5月8日 下午6:07:48
 * @author ztq
 **/
public class SingleProducerMultiConsumer05 {
	/**
	 * 单生产者，多消费者。多消费者之间不重复消费，且不同的消费者WorkPool之间存在依赖关系。
	 * 消费者1、2不重复消费消息；
	 * 消费者3、4不重复消费1或者2消费过的消息；
	 * 消费者5消费消费者3或4消费过的消息。
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		EventFactory<Order> factory = new OrderFactory();
		int ringBufferSize = 1024 * 1024;
		Disruptor<Order> disruptor = new Disruptor<Order>(factory, ringBufferSize, Executors.defaultThreadFactory(),
				ProducerType.SINGLE, new YieldingWaitStrategy());
		disruptor.handleEventsWithWorkerPool(new OrderHandler("1"), new OrderHandler("2"))
				.thenHandleEventsWithWorkerPool(new OrderHandler("3"), new OrderHandler("4")).thenHandleEventsWithWorkerPool(new OrderHandler("5"));
		disruptor.start();

		RingBuffer<Order> ringBuffer = disruptor.getRingBuffer();
		Producer producer = new Producer(ringBuffer);
		// 单生产者，生产3条数据
		for (int i = 1; i <= 3; i++) {
			producer.onData(i + "");
		}
		// 为了保证消费者线程已经启动，留足够的时间。
		Thread.sleep(1000);
		disruptor.shutdown();
	}
}
