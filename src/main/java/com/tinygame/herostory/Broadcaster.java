package com.tinygame.herostory;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * 广播消息类
 */
public final class Broadcaster {

    /**
     * 客户端信道数组，一定要使用static，否则无法实现群发
     */
    private static final ChannelGroup _channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private Broadcaster() {}

    public static void addChannel(Channel channel) {
        _channelGroup.add(channel);
    }

    public static void removeChannel(Channel channel) {
        _channelGroup.remove(channel);
    }

    public static void broadcast(Object msg) {
        _channelGroup.writeAndFlush(msg);
    }
}
