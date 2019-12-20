package com.tinygame.herostory.model;

import lombok.Data;

/**
 * 移动状态
 */
@Data
public class MoveState {

    /**
     * 起始位置X
     */
    private float fromPosX;
    /**
     * 起始位置Y
     */
    private float fromPosY;
    /**
     * 目标位置X
     */
    private float toPosX;
    /**
     * 目标位置Y
     */
    private float toPosY;
    /**
     * 开始时间
     */
    private long startTime;
}
