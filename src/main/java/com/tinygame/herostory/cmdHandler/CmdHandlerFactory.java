package com.tinygame.herostory.cmdHandler;

import com.google.protobuf.GeneratedMessageV3;
import com.tinygame.herostory.util.PackageUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 创建消息处理器工厂类
 */
@Slf4j
public final class CmdHandlerFactory {
    /**
     * 私有化类默认构造器
     */
    private CmdHandlerFactory() {
    }

    private static final Map<Class<?>, ICmdHandler<? extends GeneratedMessageV3>> _handlers = new HashMap<>();

    public static void init() {
        // 获取包名称
        final String packageName = CmdHandlerFactory.class.getPackage().getName();
        log.info("=============" + packageName);

        // 获取所有的 ICmdHandler 子类
        Set<Class<?>> clazzSet = PackageUtil.listSubClazz(packageName, true, ICmdHandler.class);

        for (Class<?> clazz : clazzSet) {
            if ((clazz.getModifiers() & Modifier.ABSTRACT) != 0) {
                //如果是抽象类
                continue;
            }

            //获取方法组
            Method[] methodArray = clazz.getDeclaredMethods();

            //消息类型
            Class<?> msgType = null;

            for (Method currMethod : methodArray) {
                if (!currMethod.getName().equals("handle")) {
                    // 如果不是 handle 方法,
                    continue;
                }

                //获取函数参数类型
                Class<?>[] parameterArray = currMethod.getParameterTypes();
                if (parameterArray.length < 2 ||
                        parameterArray[1] == GeneratedMessageV3.class ||
                        !GeneratedMessageV3.class.isAssignableFrom(parameterArray[1])) {
                    continue;
                }

                msgType = parameterArray[1];
                break;
            }

            if (null == msgType) {
                continue;
            }

            try {
                //创建指令处理器
                ICmdHandler<?> newHandler = (ICmdHandler<?>) clazz.newInstance();
                log.info("关联 {} <==> {}", msgType.getName(), clazz.getName());
                _handlers.put(msgType, newHandler);
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }
        /*_handlers.put(GameMsgProtocol.UserEntryCmd.class, new UserEntryCmdHandler());
        _handlers.put(GameMsgProtocol.WhoElseIsHereCmd.class, new WhoElseIsHereCmdHandler());
        _handlers.put(GameMsgProtocol.UserMoveToCmd.class, new UserMoveToCmdHandler());*/
    }

    public static ICmdHandler<? extends GeneratedMessageV3> getHandler(Class<?> msgClazz) {
        return _handlers.get(msgClazz);
    }

    public static void main(String[] args) {
        init();
    }
}
