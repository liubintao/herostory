package com.tinygame.herostory.rank;

import com.alibaba.fastjson.JSONObject;
import com.tinygame.herostory.ASyncOperationProcessor;
import com.tinygame.herostory.IASyncOperation;
import com.tinygame.herostory.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.List;
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
    public void getRank(Function<List<RankItem>, Void> callback) {
        if (null == callback) {
            return;
        }

        ASyncOperationProcessor.getInstance().process(new AsyncGetRank(){
            @Override
            public void doFinish() {
                callback.apply(this.getRankItemList());
            }
        });
    }

    /**
     * 异步方式获取排行榜
     */
    private class AsyncGetRank implements IASyncOperation {

        /**
         * 排名条目列表
         */
        private List<RankItem> _rankItemList = null;

        /**
         * 获取排名条目列表
         *
         * @return 排名条目列表
         */
        public List<RankItem> getRankItemList() {
            return _rankItemList;
        }

        @Override
        public void doAsync() {
            try (Jedis jedis = RedisUtil.getJedis()) {
                /**
                 * 获取字符串集合
                 */
                Set<Tuple> valSet = jedis.zrevrangeWithScores("Rank", 0, 9);


                int rankId = 0;
                for (Tuple t : valSet) {
                    //获取用户ID
                    Integer userId = Integer.valueOf(t.getElement());

                    //获取用户基本信息
                    String jsonStr = jedis.hget("User_" + userId, "BasicInfo");

                    //创建排名条目
                    RankItem newItem = new RankItem();
                    newItem.rankId = ++rankId;
                    newItem.userId = userId;
                    newItem.win = (int) t.getScore();

                    JSONObject jsonObj = JSONObject.parseObject(jsonStr);

                    newItem.userName = jsonObj.getString("userName");
                    newItem.heroAvatar = jsonObj.getString("heroAvatar");

                    _rankItemList.add(newItem);
                }
            }
        }
    }

    /**
     * 刷新排行榜
     * @param winnerId 赢家ID
     * @param loserId 输家ID
     */
    public void refreshRank(int winnerId, int loserId) {
        try (Jedis jedis = RedisUtil.getJedis()) {
            // 增加用户的胜利和失败次数
            jedis.hincrBy("User_" + winnerId, "win", 1);
            jedis.hincrBy("User_" + loserId, "loser",1);

            // 看看赢家总共赢了多少次?
            final String winStr = jedis.hget("User_" + winnerId, "win");
            int winInt = Integer.valueOf(winStr);

            //修改排名数据
            jedis.zadd("Rank", winInt, String.valueOf(winnerId));
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}
