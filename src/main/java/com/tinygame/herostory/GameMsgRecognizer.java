package com.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import com.tinygame.herostory.msg.GameMsgProtocol;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息识别器
 */
@Slf4j
public final class GameMsgRecognizer {

    private static final Map<Integer, GeneratedMessageV3> _msgCodeAndCmdClass = new HashMap<>();
    private static final Map<Class, Integer> _msgClassAndMsgCode = new HashMap<>();
    private static final Map<String, String> _msgCodeAndSimpleMsgCome = new HashMap<>();

    private GameMsgRecognizer() {
    }

    public static void init() {
        Class<?>[] innerClasses = GameMsgProtocol.class.getDeclaredClasses();

        for (Class<?> innerClz : innerClasses) {
            if (!GeneratedMessageV3.class.isAssignableFrom(innerClz)) {
                continue;
            }

            String clazzName = innerClz.getSimpleName();
            clazzName = clazzName.toLowerCase();

            for (GameMsgProtocol.MsgCode msgCode : GameMsgProtocol.MsgCode.values()) {
                String msgCodeName = msgCode.name();
                msgCodeName = msgCodeName.replace("_", "");
                msgCodeName = msgCodeName.toLowerCase();

                if (!msgCodeName.equals(clazzName)) {
                    continue;
                }

                try {
                    Object instance = innerClz.getDeclaredMethod("getDefaultInstance").invoke(innerClz);

                    log.info("{} <==> {}", innerClz.getName(), msgCode.getNumber());

                    _msgCodeAndCmdClass.put(msgCode.getNumber(), (GeneratedMessageV3) instance);

                    _msgClassAndMsgCode.put(innerClz, msgCode.getNumber());
                } catch (Exception ex) {
                    log.error(ex.getMessage(), ex);
                }
            }
        }
    }

    public static Message.Builder getBuilderByMsgCode(int msgCode) {
        if (msgCode < 0) {
            return null;
        }
        GeneratedMessageV3 msg = _msgCodeAndCmdClass.get(msgCode);
        return msg.newBuilderForType();
    }

    public static int getMsgCodeByCmd(Class<?> msgClazz) {
        if (null == msgClazz) {
            return -1;
        }

        Integer msgCode = _msgClassAndMsgCode.get(msgClazz);
        if (null == msgCode) {
            return -1;
        }

        return msgCode.intValue();
    }
}
