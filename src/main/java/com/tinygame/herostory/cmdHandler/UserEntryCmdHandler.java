package com.tinygame.herostory.cmdHandler;

import com.tinygame.herostory.Broadcaster;
import com.tinygame.herostory.model.User;
import com.tinygame.herostory.model.UserManager;
import com.tinygame.herostory.msg.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户入场指令处理器
 */
@Slf4j
public final class UserEntryCmdHandler implements ICmdHandler<GameMsgProtocol.UserEntryCmd> {

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserEntryCmd cmd) {
        if (null == ctx
                || null == cmd) {
            return;
        }

        // 获取用户 Id
        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if (null == userId) {
            return;
        }

        // 获取已有用户
        User existUser = UserManager.getUserById(userId);
        if (null == existUser) {
            log.error("用户不存在, userId = {}", userId);
            return;
        }

        // 获取英雄形象
        String heroAvatar = existUser.getHeroAvatar();

        GameMsgProtocol.UserEntryResult.Builder resultBuilder = GameMsgProtocol.UserEntryResult.newBuilder();
        resultBuilder.setUserId(userId);
        resultBuilder.setHeroAvatar(heroAvatar);

        // 构建结果并发送
        GameMsgProtocol.UserEntryResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);
    }
}
