package com.tinygame.herostory.cmdHandler;

import com.tinygame.herostory.Broadcaster;
import com.tinygame.herostory.model.User;
import com.tinygame.herostory.model.UserManager;
import com.tinygame.herostory.mq.MQProducer;
import com.tinygame.herostory.mq.VictorMsg;
import com.tinygame.herostory.msg.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户攻击命令处理器
 */
@Slf4j
public class UserAttkCmdHandler implements ICmdHandler<GameMsgProtocol.UserAttkCmd> {
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserAttkCmd cmd) {
        if (null == ctx ||
                null == cmd) {
            return;
        }

        //获取攻击者Id
        Integer attkUserId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if (null == attkUserId) {
            return;
        }

        // 获取被攻击者 Id
        int targetUserId = cmd.getTargetUserId();

        //广播攻击动作
        GameMsgProtocol.UserAttkResult attkUserResult = GameMsgProtocol.UserAttkResult.newBuilder()
                .setAttkUserId(attkUserId)
                .setTargetUserId(targetUserId)
                .build();

        Broadcaster.broadcast(attkUserResult);

        //获取被攻击者
        User targetUser = UserManager.getUserById(targetUserId);
        if (targetUser == null) {
            return;
        }

        // 在此打印线程名称
        log.info("当前线程 = {}", Thread.currentThread().getName());
        // 我们可以看到不相同的线程名称...
        // 用户 A 在攻击用户 C 的时候, 是在线程 1 里,
        // 用户 B 在攻击用户 C 的时候, 是在线程 2 里,
        // 线程 1 和线程 2 同时修改用户 C 的血量...
        // 这是要出事的节奏啊!

        // 可以根据自己的喜好写,
        // 例如加上装备加成、躲避、格挡、暴击等等...
        // 这些都属于游戏的业务逻辑了!

        int subtractHp = 10;
        targetUser.setCurrHp(targetUser.getCurrHp() - subtractHp);

        //广播减血消息
        broadcastSubtractHp(targetUserId, subtractHp);


        if(targetUser.getCurrHp() <= 0) {
            //广播死亡消息
            broadcastDie(targetUserId);

            if(!targetUser.isDied()) {
                // 设置死亡标志
                targetUser.setDied(true);

                // 发送消息到 MQ
                VictorMsg mqMsg = new VictorMsg();
                mqMsg.winnerId = attkUserId;
                mqMsg.loserId = targetUserId;
                MQProducer.sendMsg("Victor", mqMsg);
            }
        }
    }

    /**
     * 广播死亡消息
     * @param targetUserId 死亡用户ID
     */
    private void broadcastDie(int targetUserId) {
        GameMsgProtocol.UserDieResult userDieResult = GameMsgProtocol.UserDieResult.newBuilder()
                .setTargetUserId(targetUserId)
                .build();
        Broadcaster.broadcast(userDieResult);
    }

    /**
     * 广播减血消息
     * @param targetUserId 被攻击者用户Id
     * @param subtractHp 减少的血量
     */
    private void broadcastSubtractHp(int targetUserId, int subtractHp) {
        GameMsgProtocol.UserSubtractHpResult subtractHpResult = GameMsgProtocol.UserSubtractHpResult.newBuilder()
                .setTargetUserId(targetUserId)
                .setSubtractHp(subtractHp)
                .build();
        Broadcaster.broadcast(subtractHpResult);
    }
}
