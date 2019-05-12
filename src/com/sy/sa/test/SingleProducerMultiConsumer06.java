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
*
* @data 2019年5月9日 上午11:26:12
* @author ztq
**/
public class SingleProducerMultiConsumer06 {
	/**
	 * 单生产者，多消费者模式。
	 * 消费者1、2不重复消费；
	 * 消费者3、4消费消费者1或者2消费过的消息；且独立重复消费；
	 * 消费者5消费消费者3、4均消费过的消息。
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		EventFactory<Order> factory = new OrderFactory();
		int ringBufferSize = 1024*1024;
		Disruptor<Order> disruptor= new Disruptor<Order>(factory, ringBufferSize, Executors.defaultThreadFactory(), ProducerType.SINGLE, new YieldingWaitStrategy());
		disruptor.handleEventsWithWorkerPool(new OrderHandler("1"),new OrderHandler("2")).then(new OrderHandler("3"),new OrderHandler("4")).then(new OrderHandler("5"));
		disruptor.start();
		RingBuffer ringBuffer = disruptor.getRingBuffer();
		Producer producer = new Producer(ringBuffer);
		for(int i = 1;i<=3;i++) {
			producer.onData(i+ "");
		}
		Thread.sleep(1000);
		disruptor.shutdown();
		
	}
}					
