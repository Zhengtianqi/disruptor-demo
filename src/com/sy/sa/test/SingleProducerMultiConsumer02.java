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
public class SingleProducerMultiConsumer02 {
	//单生产者，多消费者模式。多消费者对于事件独立消费。例如：对于事件m，两个消费者都要对其进行消费。
    public static void main(String[] args) throws Exception {
        EventFactory<Order> factory = new OrderFactory();
        int ringBufferSize = 1024 * 1024;
        Disruptor<Order> disruptor =
                new Disruptor<Order>(factory, ringBufferSize, Executors.defaultThreadFactory(), ProducerType.SINGLE, new YieldingWaitStrategy());
        /*
         * 两个消费者创建EventHandlerGroup。该消费者需要实现EventHandler类。两个消费者对于RingBuffer中的每个事件，都独立消费一次。
         * 两个消费者在消费事件的过程中，各自独立，不产生竞争。
         */
        disruptor.handleEventsWith(new OrderHandler("1"), new OrderHandler("2"));
        disruptor.start();
        RingBuffer<Order> ringBuffer = disruptor.getRingBuffer();
        Producer producer = new Producer(ringBuffer);
        //单生产者，生产3条数据
        for (int l = 0; l < 3; l++) {
            producer.onData(l + "");
        }
        //为了保证消费者线程已经启动，留足足够的时间。
        Thread.sleep(1000);
        disruptor.shutdown();
    }
}
