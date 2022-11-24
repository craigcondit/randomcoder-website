package org.randomcoder.func;

@FunctionalInterface
public interface UncheckedFunction<T,R> {
    R call(T arg) throws Exception;
}
