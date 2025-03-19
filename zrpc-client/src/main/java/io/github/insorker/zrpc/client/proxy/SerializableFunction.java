package io.github.insorker.zrpc.client.proxy;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * Java 的 Function<T, R> 可能是匿名类或普通实现类，而 Lambda 表达式支持 序列化（Serializable），但匿名类不支持。所以这里限定类型为 Lambda 表达式。
 * @param <T>
 * @param <R>
 */
@FunctionalInterface
public interface SerializableFunction<T, R> extends Function<T, R>, Serializable {
    default String getLambdaMethodName() {
        try {
            // 反射获取 `writeReplace()` 方法
            Method writeReplace = this.getClass().getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(true);

            // 获取 SerializedLambda
            SerializedLambda lambda = (SerializedLambda) writeReplace.invoke(this);

            // 返回 Lambda 方法名称
            return lambda.getImplMethodName();
        } catch (Exception e) {
            throw new RuntimeException("Fail to get lambda method", e);
        }
    }
}
