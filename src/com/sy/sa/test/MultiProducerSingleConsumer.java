package com.sy.sa.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
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
 * @data 2019年5月9日 上午11:58:44
 * @author ztq
 **/
public class MultiProducerSingleConsumer {
	// 多生产者，单消费者版本。三个生产者独立生产消息
	public static void main(String[] args) {
		EventFactory<Order> factory = new OrderFactory();
		int ringBufferSize = 1024 * 1024;
		// producerType设置为Multi，后面才可以用多生产者模式
		Disruptor<Order> disruptor = new Disruptor(factory, ringBufferSize, Executors.defaultThreadFactory(),
				ProducerType.MULTI, new YieldingWaitStrategy());
		disruptor.handleEventsWith(new OrderHandler("1"));
		disruptor.start();

		final RingBuffer<Order> ringBuffer = disruptor.getRingBuffer();
		// 3个生产者 每个生产3条数据
		int count = 3;
		// 判断生产者是否生产完毕
		final CountDownLatch latch = new CountDownLatch(count);

		// 创建线程工厂实例
		ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("线程池-%d").build();
		// 创建线程池，核心线程数、最大线程数、空闲保持时间、队列长度、拒绝策略可自行定义
		ExecutorService executor = new ThreadPoolExecutor(5, 20, 0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>(3), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
		// CountDownLatch的计数器需要和任务数相同，执行完一个任务调用
		for (int l = 1; l <= count; l++) {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					for (int i = 0; i < 3; i++) {
						new Producer(ringBuffer).onData(Thread.currentThread().getName() + "'s " + i + "th message");
					}
					latch.countDown();
				}
			});
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}