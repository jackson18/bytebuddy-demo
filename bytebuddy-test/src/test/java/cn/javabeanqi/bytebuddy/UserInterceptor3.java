package cn.javabeanqi.bytebuddy;

import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * ========================================================
 * 日 期：2023/10/28 18:00
 * 作 者：jiabinqi
 * 版 本：1.0.0
 * 类说明：
 * ========================================================
 * 修订日期     修订人    描述
 */
public class UserInterceptor3 {

    /**
     * 被@RuntimeType 标注的方法就是拦截方法，此时方法签名可以与被拦截方法不一致
     */
    @RuntimeType
    public Object printLog(
            // 被拦截的目标对象，只有拦截实例方法时可用
            @This Object targetObj,
            // 表示被拦截的目标方法，只有拦截实例方法或静态方法可用
            @Origin Method targetMethod,
            // 目标方法的参数
            @AllArguments Object[] targetArgs,
            // 被拦截的目标对象，只有拦截实例方法时可用
            @Super Object targetObj2,
            // 用于调用目标方法
            @SuperCall Callable<?> superCall
            ) throws Exception {
        System.out.println("targetObj = " + targetObj);
        System.out.println("targetMethod = " + targetMethod);
        System.out.println("targetArgs = " + targetArgs);
        System.out.println("targetObj2 = " + targetObj2);
        System.out.println("superCall = " + superCall);

        Object call = superCall.call();
        return call;
    }

}
