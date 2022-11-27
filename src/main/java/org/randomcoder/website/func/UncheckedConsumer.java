package org.randomcoder.website.func;

@FunctionalInterface
public interface UncheckedConsumer<T> {
    void invoke(T arg) throws Exception;
}
