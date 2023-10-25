package cn.javabeanqi.bytebuddy;

/**
 * ========================================================
 * 日 期：2023/10/22 22:07
 * 作 者：jiabinqi
 * 版 本：1.0.0
 * 类说明：
 * ========================================================
 * 修订日期     修订人    描述
 */
public class UserService {

    public String queryUser(String name) {
        return "hello: " + name;
    }

    public void saveUser() {
        System.out.println("save user...");
    }

}
