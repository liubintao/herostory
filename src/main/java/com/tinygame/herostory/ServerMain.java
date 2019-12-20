package com.tinygame.herostory;

import com.tinygame.herostory.cmdHandler.CmdHandlerFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 服务器入口
 */
@Slf4j
public class ServerMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerMain.class);

    /**
     * 应用主函数
     *
     * @param args
     */
    public static void main(String[] args) {

        CmdHandlerFactory.init();
        GameMsgRecognizer.init();
        MySqlSessionFactory.init();

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)//服务器信道的处理方式
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new HttpServerCodec()) //Http服务器编解码器
                                    .addLast(new HttpObjectAggregator(65535)) //内容长度限制
                                    .addLast(new WebSocketServerProtocolHandler("/websocket")) // WebSocket 协议处理器, 在这里处理握手、ping、pong 等消息
                                    .addLast(new GameMsgDecoder()) // 自定义的消息解码器
                                    .addLast(new GameMsgEncoder()) //自定义的消息编码器
                                    .addLast(new GameMsgHandler()); // 自定义的消息处理器
                        }
                    });

            try {
                // 绑定 12345 端口,
                // 注意: 实际项目中会使用 args 中的参数来指定端口号
                ChannelFuture f = b.bind(12345).sync();

                if (f.isSuccess()) {
                    LOGGER.info("服务端启动成功");
                }

                // 等待服务器信道关闭,
                // 也就是不要立即退出应用程序, 让应用程序可以一直提供服务
                f.channel().closeFuture().sync();


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }


    }
}
