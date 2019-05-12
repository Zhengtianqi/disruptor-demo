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
 * @data 2019年5月9日 上午11:08:57
 * @author ztq
 **/
public class SingleProducerMultiConsumer07 {
	/**
	 * 单生产者，多消费者模式。
	 * 消费者1,、2独立消费每一条事件，消费者3、4不重复消费消费者1、2均处理过的事件，
	 * 消费者5消费消费者3,、4消费过的事件
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		EventFactory factory = new OrderFactory();
		int ringBufferSize = 1024*1024;	
		Disruptor<Order> disruptor = new Disruptor<Order>(factory, ringBufferSize,Executors.defaultThreadFactory(),ProducerType.SINGLE,new YieldingWaitStrategy());
		disruptor.handleEventsWith(new OrderHandler("1"),new OrderHandler("2")).thenHandleEventsWithWorkerPool(new OrderHandler("3"),new OrderHandler("4")).then(new OrderHandler("5"));
		disruptor.start();
		RingBuffer<Order> ringBuffer = disruptor.getRingBuffer();
		Producer producer = new Producer(ringBuffer);
		// 单生产者，生产3条数据
		for(int i = 1;i<=3;i++) {
			producer.onData(i + "");
		}
		// 为了保证消费者线程已经启动，留足够的时间
		Thread.sleep(1000);
		disruptor.shutdown();
	}
}
