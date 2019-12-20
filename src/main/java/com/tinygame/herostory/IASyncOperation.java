package com.tinygame.herostory;

/**
 * 异步操作回调接口
 */
public interface IASyncOperation {
    /**
     * 获取绑定id
     *
     * @return
     */
    default int bindId() {
        return 0;
    }

    /**
     * 执行异步操作
     */
    void doAsync();

    /**
     * 异步操作执行完成后的操作
     */
    default void doFinish() {
    }
}
