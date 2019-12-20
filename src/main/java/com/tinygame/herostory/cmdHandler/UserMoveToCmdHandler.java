package com.tinygame.herostory.cmdHandler;

import com.tinygame.herostory.Broadcaster;
import com.tinygame.herostory.model.MoveState;
import com.tinygame.herostory.model.User;
import com.tinygame.herostory.model.UserManager;
import com.tinygame.herostory.msg.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserMoveToCmdHandler implements ICmdHandler<GameMsgProtocol.UserMoveToCmd> {

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserMoveToCmd cmd) {

        if (null == ctx ||
                null == cmd) {
            return;
        }

        // 获取用户 Id
        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();

        // 获取移动用户
        User moveUser = UserManager.getUserById(userId);
        if (null == moveUser) {
            log.info("未找到用户, userId: {}", userId);
            return;
        }

        // 获取移动状态
        MoveState moveState = moveUser.getMoveState();
        //设置位置和开始时间
        moveState.setFromPosX(cmd.getMoveFromPosX());
        moveState.setFromPosY(cmd.getMoveFromPosY());
        moveState.setToPosX(cmd.getMoveToPosX());
        moveState.setToPosY(cmd.getMoveToPosY());
        moveState.setStartTime(System.currentTimeMillis());

        GameMsgProtocol.UserMoveToResult result = GameMsgProtocol.UserMoveToResult
                .newBuilder()
                .setMoveUserId(userId)
                .setMoveFromPosX(moveState.getFromPosX())
                .setMoveFromPosY(moveState.getFromPosY())
                .setMoveToPosX(moveState.getToPosX())
                .setMoveToPosY(moveState.getToPosY())
                .setMoveStartTime(moveState.getStartTime())
                .build();

        Broadcaster.broadcast(result);
    }
}
