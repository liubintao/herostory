package com.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import com.tinygame.herostory.model.UserManager;
import com.tinygame.herostory.msg.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 对解码后的消息进行处理的处理器
 */
public class GameMsgHandler extends SimpleChannelInboundHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameMsgHandler.class);


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        Broadcaster.addChannel(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        Broadcaster.removeChannel(ctx.channel());

        //拿到当前退出的用户Id
        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();

        if (null == userId) {
            return;
        }

        UserManager.removeUserById(userId);

        //构造当前用户退出的消息广播至其他用户
        GameMsgProtocol.UserQuitResult result = GameMsgProtocol.UserQuitResult
                .newBuilder()
                .setQuitUserId(userId)
                .build();
        Broadcaster.broadcast(result);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof GeneratedMessageV3) {
            // 通过主线程处理器处理消息
            MainThreadProcessor.getInstance().process(ctx, (GeneratedMessageV3) msg);
        }
    }
}
