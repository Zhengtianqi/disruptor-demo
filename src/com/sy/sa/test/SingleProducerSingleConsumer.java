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
* 单生产者单消费者
* @data 2019年5月8日 下午5:55:30
* @author ztq
**/
public class SingleProducerSingleConsumer {
	//单生产者模式，单消费者模式
    public static void main(String[] args) throws Exception {
        EventFactory<Order> factory = new OrderFactory();
        int ringBufferSize = 1024 * 1024;
        Disruptor<Order> disruptor =
                new Disruptor<Order>(factory, ringBufferSize, Executors.defaultThreadFactory(), ProducerType.SINGLE, new YieldingWaitStrategy());
        //设置一个消费者
        disruptor.handleEventsWith(new OrderHandler("1"));
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
