package com.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import com.tinygame.herostory.cmdHandler.CmdHandlerFactory;
import com.tinygame.herostory.cmdHandler.ICmdHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 主线程处理器
 */
@Slf4j
public final class MainThreadProcessor {

    //单例对象
    private static final MainThreadProcessor _instance = new MainThreadProcessor();
    /**
     * 创建一个单线程
     */
    private static final ExecutorService _es = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r);
        t.setName("MainThreadProcessor");
        return t;
    });

    /**
     * 私有化默认构造器
     */
    private MainThreadProcessor() {
    }

    /**
     * 获取单例对象
     *
     * @return 单例对象
     */
    public static MainThreadProcessor getInstance() {
        return _instance;
    }

    /**
     * 处理客户端消息
     *
     * @param ctx 客户端信道上下文
     * @param msg 消息对象
     */
    public void process(ChannelHandlerContext ctx, GeneratedMessageV3 msg) {
        if (null == ctx ||
                null == msg) {
            return;
        }

        //获取消息类
        Class<? extends GeneratedMessageV3> msgClass = msg.getClass();

        log.info("服务端收到消息, msgClz = " + msgClass.getName() + ", msg = " + msg);

        _es.submit(() -> {
            //获取指令处理器
            ICmdHandler<? extends GeneratedMessageV3> handler = CmdHandlerFactory.getHandler(msgClass);
            if (null == handler) {
                log.error("未找到相对应的指令处理器, msgClass = {}", msgClass.getName());
                return;
            }

            try {
                //处理指令
                handler.handle(ctx, cast(msg));
                // 注意: 这里一定要套在 try ... catch ... 块里!
                // 避免 handler 报错导致线程终止
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        });
    }

    public void process(Runnable r) {
        _es.submit(r);
    }

    private static <TCmd extends GeneratedMessageV3> TCmd cast(Object msg) {
        if (null == msg) {
            return null;
        }
        return (TCmd) msg;
    }
}
