/**
* ClassName : Recv.java
* Create on ：2016年5月23日
* Copyrights 2016 guanfl All rights reserved.
* Email : guanfl@163.com
*/
package org.rabbitmq.work;

import org.rabbitmq.util.ConnectionUtil;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;

/** 接收者 / 消费者模型 */
public class RecvSecond {
    private static final String QUEUE_NAME = "work_queue";

    /**
     * <p>
     * The extra DefaultConsumer is a class implementing the Consumer interface
     * we'll use to buffer the messages pushed to us by the server.
     * </p>
     * 
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {
        // 获取连接
        Connection connection = ConnectionUtil.getConnection();
        // 获取通道
        Channel channel = connection.createChannel();
        // 声明队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        // 设置同一时刻服务器只会发送一条消息给消费者
        channel.basicQos(1);
        
        // 定义队列消费者
        QueueingConsumer consumer = new QueueingConsumer(channel);
        // 监听队列
        channel.basicConsume(QUEUE_NAME, false, consumer);//手动确认消息被消费的状态

        // 阻塞式获取队列消息
        while (true) {
            Delivery delivery = consumer.nextDelivery();
            String recv = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + recv + "'");
            //休息1秒
            Thread.sleep(1000);
            //返回消息确认状态
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        }

    }
}
