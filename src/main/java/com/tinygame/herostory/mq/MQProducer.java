package com.tinygame.herostory.mq;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

/**
 * 消息队列生产者
 */
@Slf4j
public final class MQProducer {
    /**
     * 生产者
     */
    private static DefaultMQProducer _producer = null;

    /**
     * 私有化类默认构造器
     */
    private MQProducer() {
    }

    public static void init() {
        try {
            //创建生产者
            DefaultMQProducer producer = new DefaultMQProducer("herostory");
            //指定 nameserver地址
            producer.setNamesrvAddr("127.0.0.1:9876");
            //启动生产者
            producer.start();
            producer.setRetryTimesWhenSendAsyncFailed(3);

            _producer = producer;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    /**
     * 发送消息
     *
     * @param topic 消息主题
     * @param msg   消息对象
     */
    public static void sendMsg(String topic, Object msg) {
        if (null == topic ||
                null == msg) {
            return;
        }

        if (null == _producer) {
            log.error("_producer 尚未初始化");
        }

        Message mqMsg = new Message();
        mqMsg.setTopic(topic);
        mqMsg.setBody(JSONObject.toJSONBytes(msg));

        try {
            _producer.send(mqMsg);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}
