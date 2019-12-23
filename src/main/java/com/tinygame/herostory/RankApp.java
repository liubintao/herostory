package com.tinygame.herostory;

import com.tinygame.herostory.mq.MQConsumer;
import com.tinygame.herostory.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 排行榜应用程序
 */
@Slf4j
public class RankApp {

    /**
     * 应用主函数
     *
     * @param argvArray 命令行参数数组
     */
    static public void main(String[] argvArray) {
        RedisUtil.init();
        MQConsumer.init();

        log.info("排行榜应用程序启动成功!");
    }
}
