package com.tinygame.herostory.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户管理类
 */
public final class UserManager {
    private UserManager() {

    }

    /**
     * 用户字典
     */
    private static final Map<Integer, User> _userMap = new ConcurrentHashMap<>();

    public static void addUser(User user) {
        _userMap.put(user.getUserId(), user);
    }

    public static void removeUserById(int userId) {
        _userMap.remove(userId);
    }

    public static User getUserById(int userId) {
        return _userMap.get(userId);
    }

    public static Collection<User> listUser() {
        return _userMap.values();
    }
}
