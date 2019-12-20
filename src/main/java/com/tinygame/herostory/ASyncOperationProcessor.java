package com.tinygame.herostory;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 异步处理器
 */
@Slf4j
public final class ASyncOperationProcessor {
    /**
     * 单例对象
     */
    private static final ASyncOperationProcessor _instance = new ASyncOperationProcessor();

    /**
     * 单线程线程池
     */
    private final ExecutorService[] _es = new ExecutorService[8];

    /**
     * 私有化类默认构造器
     */
    private ASyncOperationProcessor() {
        for (int i = 0; i < _es.length; i++) {
            final String threadName = "ASyncOperationProcessor" + i;
            _es[i] = Executors.newSingleThreadExecutor(r -> {
                Thread t = new Thread(r);
                t.setName(threadName);
                return t;
            });
        }
    }

    /**
     * 获取单例对象
     *
     * @return 单例对象
     */
    public static ASyncOperationProcessor getInstance() {
        return _instance;
    }


    /**
     * 提交执行任务
     */
    public void process(IASyncOperation operation) {
        int index = operation.bindId() % _es.length;
        _es[index].submit(() -> {
            try {
                operation.doAsync();

                MainThreadProcessor.getInstance().process(operation::doFinish);
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        });
    }
}
