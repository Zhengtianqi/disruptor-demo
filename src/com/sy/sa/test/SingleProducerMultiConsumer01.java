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
public class SingleProducerMultiConsumer01 {
	//单生产者，多消费者，但多消费者间形成依赖关系，每个依赖节点单线程
    public static void main(String[] args) throws Exception {
        EventFactory<Order> factory = new OrderFactory();
        int ringBufferSize = 1024 * 1024;
        Disruptor<Order> disruptor =
                new Disruptor<Order>(factory, ringBufferSize, Executors.defaultThreadFactory(), ProducerType.SINGLE, new YieldingWaitStrategy());
        //多个消费者间形成依赖关系，每个依赖节点的消费者为单线程
        disruptor.handleEventsWith(new OrderHandler("1")).then(new OrderHandler("2"), new OrderHandler("3")).then(new OrderHandler("4"));
        disruptor.start();
        RingBuffer<Order> ringBuffer = disruptor.getRingBuffer();
        Producer producer = new Producer(ringBuffer);
        //单生产者，生产3条数据
        for (int l = 0; l < 3; l++) {
            producer.onData(l + "");
        }
        //为了保证消费者线程已经启动，留足足够的时间
        Thread.sleep(1000);
        disruptor.shutdown();
    }
}
