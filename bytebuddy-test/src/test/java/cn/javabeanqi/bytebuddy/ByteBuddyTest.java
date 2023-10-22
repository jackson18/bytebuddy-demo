package cn.javabeanqi.bytebuddy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.dynamic.DynamicType;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * ========================================================
 * 日 期：2023/10/22 21:40
 * 作 者：jiabinqi
 * 版 本：1.0.0
 * 类说明：
 * ========================================================
 * 修订日期     修订人    描述
 */
public class ByteBuddyTest {

    private String path;

    @Before
    public void init() {
        path = ByteBuddyTest.class.getClassLoader().getResource("").getPath();
        System.out.println("path = " + path);
    }

    /**
     * 生成一个类
     * Object$ByteBuddy$VQy9xVU2
     */
    @Test
    public void createClassTest() throws IOException {
        // unloaded 代表生成的字节码
        DynamicType.Unloaded<UserService> unloaded = new ByteBuddy()
                // 指定类的生成策略
                .with(new NamingStrategy.SuffixingRandom("javabeanqi"))
                .subclass(UserService.class)
                .name("cn.javabeanqi.MyUserService")
                .make();
        // 保存获取生成类的字节码
        unloaded.saveIn(new File(path));
    }

}
