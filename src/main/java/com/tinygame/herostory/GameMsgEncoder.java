package com.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自定义消息编码器
 */
@Slf4j
public class GameMsgEncoder extends ChannelOutboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameMsgEncoder.class);

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (null == msg || !(msg instanceof GeneratedMessageV3)) {
            super.write(ctx, msg, promise);
            return;
        }

        int msgCode = GameMsgRecognizer.getMsgCodeByCmd(msg.getClass());

        if (msgCode <= -1) {
            log.error("无法支持的消息, msgClazz = " + msg.getClass().getName());
            return;
        }

        GeneratedMessageV3 messageV3 = (GeneratedMessageV3) msg;
        byte[] byteArray = messageV3.toByteArray();

        ByteBuf buffer = ctx.alloc().buffer();
        buffer.writeShort((short) 0);
        buffer.writeShort((short) msgCode);
        buffer.writeBytes(byteArray);

        BinaryWebSocketFrame frame = new BinaryWebSocketFrame(buffer);
        super.write(ctx, frame, promise);
    }
}
