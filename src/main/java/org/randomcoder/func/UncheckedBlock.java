package org.randomcoder.func;

@FunctionalInterface
public interface UncheckedBlock {
    void call() throws Exception;
}
