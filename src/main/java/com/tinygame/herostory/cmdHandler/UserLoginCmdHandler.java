package com.tinygame.herostory.cmdHandler;

import com.tinygame.herostory.login.LoginService;
import com.tinygame.herostory.model.User;
import com.tinygame.herostory.model.UserManager;
import com.tinygame.herostory.msg.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户登录指令处理器
 */
@Slf4j
public class UserLoginCmdHandler implements ICmdHandler<GameMsgProtocol.UserLoginCmd> {

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserLoginCmd cmd) {
        if (null == ctx ||
                null == cmd) {
            return;
        }

        String userName = cmd.getUserName();
        String password = cmd.getPassword();

        log.info(
                "用户登陆, userName = {}, password = {}",
                userName,
                password
        );


        try {
            LoginService.getInstance().login(userName, password, (userEntity) -> {
                if (null == userEntity) {
                    log.error("用户登陆失败, userName = {}", cmd.getUserName());
                } else {
                    log.info(
                            "用户登陆成功, userId = {}, userName = {}",
                            userEntity.userId,
                            userEntity.userName
                    );

                    log.info("当前线程 = " + Thread.currentThread().getName());

                    //新建用户
                    User user = User.builder()
                            .userId(userEntity.userId)
                            .userName(userEntity.userName)
                            .heroAvatar(userEntity.heroAvatar)
                            .currHp(100)
                            .build();

                    // 并将用户加入管理器
                    UserManager.addUser(user);

                    //将用户id附着到channel
                    ctx.channel().attr(AttributeKey.valueOf("userId")).set(user.getUserId());

                    // 登陆结果构建者
                    GameMsgProtocol.UserLoginResult result = GameMsgProtocol.UserLoginResult
                            .newBuilder()
                            .setUserId(userEntity.userId)
                            .setUserName(userEntity.userName)
                            .setHeroAvatar(userEntity.heroAvatar)
                            .build();

                    // 构建结果并发送
                    ctx.writeAndFlush(result);
                }
                return null;
            });
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return;
        }


    }
}
