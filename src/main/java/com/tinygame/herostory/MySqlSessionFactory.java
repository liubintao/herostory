package com.tinygame.herostory;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

/**
 * Mysql会话工厂
 */
@Slf4j
public final class MySqlSessionFactory {
    /**
     * MyBatis Sql 会话工厂
     */
    private static SqlSessionFactory _sqlSessionFactory;

    /**
     * 私有化类默认构造器
     */
    private MySqlSessionFactory() {
    }

    public static void init() {
        try {
            _sqlSessionFactory =
                    new SqlSessionFactoryBuilder()
                            .build(Resources.getResourceAsStream("MyBatisConfig.xml"));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 开启 MySql 会话
     *
     * @return MySql 会话
     */
    public static SqlSession openSession() {
        if (null == _sqlSessionFactory) {
            throw new RuntimeException("_sqlSessionFactory尚未初始化！");
        }
        return _sqlSessionFactory.openSession(true);
    }
}
