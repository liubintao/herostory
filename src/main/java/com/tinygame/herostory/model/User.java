package com.tinygame.herostory.model;

import lombok.Builder;
import lombok.Data;

/**
 * 用户
 */
@Builder
@Data
public class User {
    /**
     * 用户Id
     */
    private Integer userId;
    /**
     * 用户名称
     */
    public String userName;
    /**
     * 英雄形象
     */
    private String heroAvatar;
    /**
     * 血量
     */
    private int currHp;
    /**
     * 移动状态
     */
    private final MoveState moveState = new MoveState();
    /**
     * 已死亡
     */
    private boolean died;
}
