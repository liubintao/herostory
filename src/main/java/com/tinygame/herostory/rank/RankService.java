package com.tinygame.herostory.rank;

import com.tinygame.herostory.IASyncOperation;
import com.tinygame.herostory.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.Set;
import java.util.function.Function;

/**
 * 排行榜服务
 */
@Slf4j
public final class RankService {

    /**
     * 单例对象
     */
    private static final RankService _instance = new RankService();

    /**
     * 私有化类默认构造器
     */
    private RankService() {
    }

    /**
     * 获取单例对象
     *
     * @return
     */
    public static RankService getInstance() {
        return _instance;
    }

    /**
     * 获取排行榜
     *
     * @param callback
     */
    public void getRank(Function<?, Void> callback) {
        if (null == callback) {
            return;
        }


    }

    private class RankSyncOperation implements IASyncOperation {

        @Override
        public void doAsync() {
            try (Jedis jedis = RedisUtil.getJedis()) {
                Set<Tuple> valSet = jedis.zrevrangeWithScores("Rank", 0, 9);
                for (Tuple t : valSet) {
                    Integer userId = Integer.valueOf(t.getElement());
                }
            }
        }
    }
}
