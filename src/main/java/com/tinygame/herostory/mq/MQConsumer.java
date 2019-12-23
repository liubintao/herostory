package com.tinygame.herostory.mq;

import com.alibaba.fastjson.JSONObject;
import com.tinygame.herostory.rank.RankService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * 消息队列消费者
 */
@Slf4j
public final class MQConsumer {
    /**
     * 私有化类默认构造器
     */
    private MQConsumer() {
    }

    /**
     * 初始化
     */
    public static void init() {
        // 创建消息队列消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("herostory");
        //设置 nameServer 地址
        consumer.setNamesrvAddr("127.0.0.1:9876");
        try {
            consumer.subscribe("Victor", "*");

            //注册回调
            consumer.registerMessageListener((MessageListenerConcurrently) (msgExtList, ctx) -> {
                for (MessageExt msgExt : msgExtList) {
                    // 解析战斗结果消息
                    VictorMsg mqMsg = JSONObject.parseObject(
                            msgExt.getBody(),
                            VictorMsg.class
                    );

                    log.info(
                            "从消息队列中收到战斗结果, winnerId = {}, loserId = {}",
                            mqMsg.winnerId,
                            mqMsg.loserId
                    );

                    // 刷新排行榜
                    RankService.getInstance().refreshRank(mqMsg.winnerId, mqMsg.loserId);
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            });

            //启动消费者
            consumer.start();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}
