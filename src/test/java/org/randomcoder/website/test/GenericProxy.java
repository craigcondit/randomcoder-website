package org.randomcoder.website.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class GenericProxy implements InvocationHandler {

    private final Object target;

    public GenericProxy(Object target) {
        this.target = target;
    }

    public static Object proxy(Object target, Class<?>... interfaces) {
        return Proxy.newProxyInstance(GenericProxy.class.getClassLoader(), interfaces, new GenericProxy(target));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(target, args);
    }

}
