package com.tinygame.herostory.login;

import com.alibaba.fastjson.JSONObject;
import com.tinygame.herostory.ASyncOperationProcessor;
import com.tinygame.herostory.IASyncOperation;
import com.tinygame.herostory.MySqlSessionFactory;
import com.tinygame.herostory.login.db.IUserDao;
import com.tinygame.herostory.login.db.UserEntity;
import com.tinygame.herostory.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import redis.clients.jedis.Jedis;

import java.util.function.Function;

/**
 * 登录服务
 */
@Slf4j
public class LoginService {

    /**
     * 单例对象
     */
    private static final LoginService _instance = new LoginService();

    /**
     * 私有化类默认构造器
     */
    private LoginService() {
    }

    /**
     * 获取单例对象
     *
     * @return 单例对象
     */
    public static LoginService getInstance() {
        return _instance;
    }

    /**
     * 用户登陆
     *
     * @param userName 用户名称
     * @param password 密码
     * @return 用户实体
     */
    public void login(String userName, String password, Function<UserEntity, Void> callback) {
        if (null == userName ||
                null == password) {
        }

        ASyncOperationProcessor.getInstance().process(new LoginAsyncOperation(userName, password) {
            @Override
            public void doFinish() {
                callback.apply(this.getUserEntity());
            }
        });
    }

    private class LoginAsyncOperation implements IASyncOperation {
        /**
         * 用户姓名
         */
        private String userName;
        /**
         * 用户密码
         */
        private String password;

        private UserEntity _userEntity = null;

        public LoginAsyncOperation(String userName, String password) {
            this.userName = userName;
            this.password = password;
        }

        public UserEntity getUserEntity() {
            return _userEntity;
        }

        @Override
        public int bindId() {
            return userName.charAt(userName.length() - 1);
        }

        @Override
        public void doAsync() {
            try (SqlSession mySqlSession = MySqlSessionFactory.openSession()) {
                // 获取 DAO 对象,
                // 注意: 这个 IUserDao 接口咱们是没有具体实现的,
                // 但如果你听过前面的课,
                // 你可能会猜到这里面究竟发生了什么... :)
                //其实就是用反射+javaassist等技术动态生成了实现类
                IUserDao dao = mySqlSession.getMapper(IUserDao.class);

                // 看看当前线程
                log.info("当前线程 = {}", Thread.currentThread().getName());

                // 根据用户名称获取用户实体
                UserEntity userEntity = dao.getUserByName(userName);

                if (null != userEntity) {
                    // 判断用户密码
                    if (!password.equals(userEntity.password)) {
                        // 用户密码错误,
                        log.error(
                                "用户密码错误, userId = {}, userName = {}",
                                userEntity.userId,
                                userName
                        );

                        throw new RuntimeException("用户密码错误");
                    }
                } else {
                    // 如果用户实体为空, 则新建用户!
                    userEntity = new UserEntity();
                    userEntity.userName = userName;
                    userEntity.password = password;
                    userEntity.heroAvatar = "Hero_Shaman"; // 默认使用萨满

                    // 将用户实体添加到数据库
                    dao.insertInto(userEntity);
                }
                _userEntity = userEntity;

                // 更新 Redis 中的用户基本信息
                LoginService.getInstance().updateUserBasicInfoInRedis(userEntity);
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }
    }

    /**
     * 更新 Redis 中的用户基本信息
     *
     * @param userEntity 用户实体
     */
    private void updateUserBasicInfoInRedis(UserEntity userEntity) {
        if (null == userEntity ||
                userEntity.userId <= 0) {
            return;
        }

        try (Jedis redis = RedisUtil.getJedis()) {
            // 获取用户 Id
            int userId = userEntity.userId;

            // 创建 JSON 对象
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("userId", userId);
            jsonObj.put("userName", userEntity.userName);
            jsonObj.put("heroAvatar", userEntity.heroAvatar);

            // 更新 Redis 数据
            redis.hset("User_" + userId, "BasicInfo", jsonObj.toJSONString());
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}
