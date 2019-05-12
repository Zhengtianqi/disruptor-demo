package com.sy.sa.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * 使用disruptor的步骤
 * 了解更多可以访问:  https://www.cnblogs.com/pku-liuqiang/p/8544700.html
 * 
 * @data 2019年5月8日 下午5:15:12
 * @author ztq
 **/
public class DisruptorTest {
	public static void main(String[] args) {
		// step_4 定义用于事件处理的线程池
		ExecutorService executor = Executors.newSingleThreadExecutor();
		// 指定等待策略
		WaitStrategy Yielding_Wait = new YieldingWaitStrategy();

		// step_5 启动Disruptor
		// 预先调用所有事件以填充RingBuffer
		EventFactory<LongEvent> eventFactory = new LongEventFactory();
		// RingBuffer大小，必须是2的N次方
		int ringBufferSize = 1024 * 1024;
		// step_6 消费事件
		Disruptor<LongEvent> disruptor = new Disruptor<LongEvent>(eventFactory, ringBufferSize, executor,
				ProducerType.SINGLE, Yielding_Wait);
		EventHandler<LongEvent> eventHandler = new LongEventHandler();
		disruptor.handleEventsWith(eventHandler);
		disruptor.start();
		// step_7 发布事件；
		RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
		long sequence = ringBuffer.next();// 请求下一个事件序号；

		try {
			LongEvent event = ringBuffer.get(sequence);// 获取该序号对应的事件对象；
			long data = getEventData();// 获取要通过事件传递的业务数据；
			event.set(data);
		} finally {
			ringBuffer.publish(sequence);// 发布事件；
		}

	}

	// 定义的生产者生产数据，只生产一条999
	private static long getEventData() {
		return 999;
	}

	// step_3:定义事件处理的具体实现
	public static class LongEventHandler implements EventHandler<LongEvent> {

		@Override
		public void onEvent(LongEvent event, long sequence, boolean endOfBatch) throws Exception {
			System.out.println("Event: " + event.value);
		}

	}

	// step_2: 定义事件工厂
	public static class LongEventFactory implements EventFactory<LongEvent> {
		@Override
		public LongEvent newInstance() {
			return new LongEvent();
		}

	}

	// step_1:定义事件
	public static class LongEvent {
		private long value;

		public void set(long value) {
			this.value = value;
		}
	}
}
