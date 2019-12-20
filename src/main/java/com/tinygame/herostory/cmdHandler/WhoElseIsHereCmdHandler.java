package com.tinygame.herostory.cmdHandler;

import com.tinygame.herostory.model.MoveState;
import com.tinygame.herostory.model.User;
import com.tinygame.herostory.model.UserManager;
import com.tinygame.herostory.msg.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;

public class WhoElseIsHereCmdHandler implements ICmdHandler<GameMsgProtocol.WhoElseIsHereCmd> {

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.WhoElseIsHereCmd cmd) {

        if (null == ctx ||
                null == cmd) {
            return;
        }

        GameMsgProtocol.WhoElseIsHereResult.Builder resultBuilder = GameMsgProtocol.WhoElseIsHereResult.newBuilder();

        for (User user : UserManager.listUser()) {
            if (null == user) {
                continue;
            }

            GameMsgProtocol.WhoElseIsHereResult.UserInfo.Builder builder =
                    GameMsgProtocol.WhoElseIsHereResult.UserInfo.newBuilder();
            builder.setUserId(user.getUserId());
            builder.setHeroAvatar(user.getHeroAvatar());
            // 构建移动状态
            MoveState mvState = user.getMoveState();
            GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.Builder mvBuilder =
                    GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.newBuilder();
            mvBuilder.setFromPosX(mvState.getFromPosX());
            mvBuilder.setFromPosY(mvState.getFromPosY());
            mvBuilder.setToPosX(mvState.getToPosX());
            mvBuilder.setToPosY(mvState.getToPosY());
            mvBuilder.setStartTime(mvState.getStartTime());
            builder.setMoveState(mvBuilder);
            resultBuilder.addUserInfo(builder);
        }

        GameMsgProtocol.WhoElseIsHereResult result = resultBuilder.build();
        ctx.writeAndFlush(result);
    }
}
