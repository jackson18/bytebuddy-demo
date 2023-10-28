package cn.javabeanqi.bytebuddy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.Morph;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;

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

    /**
     * 插入新方法
     */
    @Test
    public void createNewMethodTest() throws IOException {
        // unloaded 代表生成的字节码
        DynamicType.Unloaded<UserService> unloaded = new ByteBuddy()
                .redefine(UserService.class)
                .name("cn.javabeanqi.MyUserService3")
                // 方法名、返回类型、修饰符
                .defineMethod("getUserById", String.class, Modifier.PUBLIC + Modifier.STATIC)
                // 指定方法的参数
                .withParameters(Long.class)
                // 指定方法的返回
                .intercept(FixedValue.value("hello new method..."))
                .make();
        // 保存获取生成类的字节码
        unloaded.saveIn(new File(path));
    }

    /**
     * 插入新属性
     */
    @Test
    public void insertFieldTest() throws IOException {
        // unloaded 代表生成的字节码
        DynamicType.Unloaded<UserService> unloaded = new ByteBuddy()
                .redefine(UserService.class)
                .name("cn.javabeanqi.MyUserService4")
                // 定义属性
                .defineField("name", String.class, Modifier.PRIVATE)
                // 定义get\set接口
                .implement(NameInterface.class)
                // 生成接口实现
                .intercept(FieldAccessor.ofField("name"))
                .make();
        // 保存获取生成类的字节码
        unloaded.saveIn(new File(path));
    }

    /**
     * 方法委托
     */
    @Test
    public void methodTrustTest() throws IOException, InstantiationException, IllegalAccessException {
        // unloaded 代表生成的字节码
        DynamicType.Unloaded<UserService> unloaded = new ByteBuddy()
                .subclass(UserService.class)
                .name("cn.javabeanqi.MyUserService5")
                .method(named("queryUser"))
                // 委托给 UserInterceptor中与拦截方法同签名的静态方法
//                .intercept(MethodDelegation.to(UserInterceptor.class))
                // 委托给UserInterceptor2中与拦截方法同签名的成员方法
//                .intercept(MethodDelegation.to(new UserInterceptor2()))
                // 通过 bytebuddy 的注解来指定增强的方法
                .intercept(MethodDelegation.to(new UserInterceptor3()))
                .make();

        DynamicType.Loaded<UserService> loaded = unloaded.load(getClass().getClassLoader());
        Class<? extends UserService> clazz = loaded.getLoaded();
        UserService userService = clazz.newInstance();
        String result = userService.queryUser("张三");
        System.out.println("result = " + result);

        // 保存获取生成类的字节码
        unloaded.saveIn(new File(path));
    }

    /**
     * 动态修改方法入参
     * 1、自定义 MyCallable
     * 2、在 UserInterceptor4 中使用 @Morph 代替 @SuperCall
     * 3、指定拦截器前需要先调用 withBinders
     */
    @Test
    public void updateMethodArgsTest() throws IOException, InstantiationException, IllegalAccessException {
        // unloaded 代表生成的字节码
        DynamicType.Unloaded<UserService> unloaded = new ByteBuddy()
                .subclass(UserService.class)
                .name("cn.javabeanqi.MyUserService5")
                .method(named("queryUser"))
                .intercept(MethodDelegation.
                        withDefaultConfiguration().
                        // 指定参数类型是 MyCallable
                        withBinders(Morph.Binder.install(MyCallable.class)).
                        to(new UserInterceptor4())
                )
                .make();

        DynamicType.Loaded<UserService> loaded = unloaded.load(getClass().getClassLoader());
        Class<? extends UserService> clazz = loaded.getLoaded();
        UserService userService = clazz.newInstance();
        String result = userService.queryUser("张三");
        System.out.println("result = " + result);

        // 保存获取生成类的字节码
        unloaded.saveIn(new File(path));
    }

}
