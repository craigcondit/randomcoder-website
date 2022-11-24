package org.randomcoder.func;

@FunctionalInterface
public interface UncheckedConsumer<T> {
    void invoke(T arg) throws Exception;
}
