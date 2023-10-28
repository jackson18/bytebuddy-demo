package cn.javabeanqi.bytebuddy;

import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
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
public class UserInterceptor4 {

    /**
     * 被@RuntimeType 标注的方法就是拦截方法，此时方法签名可以与被拦截方法不一致
     */
    @RuntimeType
    public Object printLog(
            // 目标方法的参数
            @AllArguments Object[] targetArgs,
            // 用于调用目标方法
            @Morph MyCallable superCall
            ) {
        System.out.println("targetArgs = " + targetArgs);
        System.out.println("superCall = " + superCall);

        if (targetArgs != null && targetArgs.length > 0) {
            targetArgs[0] = targetArgs[0] + "(Test)";
        }

        Object call = superCall.call(targetArgs);
        return call;
    }

}
