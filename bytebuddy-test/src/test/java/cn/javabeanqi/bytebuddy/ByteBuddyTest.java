package cn.javabeanqi.bytebuddy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FixedValue;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.returns;

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

    /**
     * 对实例方法进行插桩
     */
    @Test
    public void interceptMethodTest() throws IOException, InstantiationException, IllegalAccessException {
        // unloaded 代表生成的字节码
        DynamicType.Unloaded<UserService> unloaded = new ByteBuddy()
                .subclass(UserService.class)
                .name("cn.javabeanqi.MyUserService2")
                .method(named("toString"))
                .intercept(FixedValue.value("hello world!!!"))
                .make();
        // loaded 代表生成的字节码已经加载到jvm
        DynamicType.Loaded<UserService> loaded = unloaded.load(getClass().getClassLoader());
        // 获取Class对象
        Class<? extends UserService> clazz = loaded.getLoaded();
        UserService userService = clazz.newInstance();
        String result = userService.toString();
        System.out.println("result = " + result);
        System.out.println("clazz.getClassLoader() = " + clazz.getClassLoader());
        // 保存获取生成类的字节码
        unloaded.saveIn(new File(path));
    }

    /**
     * 动态增强的3种方式
     * 1、subclass 为目标类（即被增强的类）生成一个子类，在子类方法中插入动态代码
     * 2、rebase 变基，保留原方法 （不再继承）
     * 3、redefine 原方法不再保留
     */
    @Test
    public void dynamicEnhancedTest() throws IOException {
        // unloaded 代表生成的字节码
        DynamicType.Unloaded<UserService> unloaded = new ByteBuddy()
//                .subclass(UserService.class)
//                .rebase(UserService.class)
                .redefine(UserService.class)
                .name("cn.javabeanqi.MyUserService2")
                .method(named("queryUser").and(
                        returns(TypeDescription.CLASS).or(returns(TypeDescription.STRING))
                ))
                .intercept(FixedValue.nullValue())
                .method(named("saveUser").and(
                        returns(TypeDescription.VOID).or(returns(TypeDescription.VOID))
                ))
                .intercept(FixedValue.value(TypeDescription.VOID))
                .make();
        // 保存获取生成类的字节码
        unloaded.saveIn(new File(path));
    }

}
