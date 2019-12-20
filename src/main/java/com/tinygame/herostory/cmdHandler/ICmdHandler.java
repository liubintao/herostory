package com.tinygame.herostory.cmdHandler;

import com.google.protobuf.GeneratedMessageV3;
import com.tinygame.herostory.msg.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;

public interface ICmdHandler<TCmd extends GeneratedMessageV3> {
    void handle(ChannelHandlerContext ctx, TCmd cmd);
}
