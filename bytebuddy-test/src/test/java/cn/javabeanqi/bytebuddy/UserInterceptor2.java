package cn.javabeanqi.bytebuddy;

import java.util.UUID;

/**
 * ========================================================
 * 日 期：2023/10/28 18:00
 * 作 者：jiabinqi
 * 版 本：1.0.0
 * 类说明：
 * ========================================================
 * 修订日期     修订人    描述
 */
public class UserInterceptor2 {

    public String queryUser(String name) {
        return "用户名称：" + name + ", uuid: " + UUID.randomUUID();
    }

}
