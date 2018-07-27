package org.randomcoder.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class GenericProxy implements InvocationHandler {
	private final Object _target;

	public GenericProxy(Object target) {
		_target = target;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return method.invoke(_target, args);
	}

	public static Object proxy(Object target, Class<?>... interfaces) {
		return Proxy.newProxyInstance(GenericProxy.class.getClassLoader(), interfaces, new GenericProxy(target));
	}

}
