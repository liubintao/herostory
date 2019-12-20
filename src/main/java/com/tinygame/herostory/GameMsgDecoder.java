package com.tinygame.herostory;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义的消息解码器
 */
@Slf4j
public class GameMsgDecoder extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //防御式编程,不是我想要的消息不处理
        if (!(msg instanceof BinaryWebSocketFrame)) {
            return;
        }

        // WebSocket 二进制消息会通过 HttpServerCodec 解码成 BinaryWebSocketFrame 类对象
        BinaryWebSocketFrame frame = (BinaryWebSocketFrame) msg;
        ByteBuf content = frame.content();

        content.readShort(); //读取消息的长度
        int msgCode = content.readShort();//读取消息的编号

        Message.Builder builder = GameMsgRecognizer.getBuilderByMsgCode(msgCode);

        if (null == builder) {
            log.error("无法识别的消息");
            return;
        }

        // 拿到消息体
        byte[] msgBody = new byte[content.readableBytes()];
        content.readBytes(msgBody);

        builder.clear();
        builder.mergeFrom(msgBody);
        Message message = builder.build();

        if (null != message) {
            ctx.fireChannelRead(message);
        }
    }
}
