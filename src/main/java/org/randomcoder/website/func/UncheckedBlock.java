package org.randomcoder.website.func;

@FunctionalInterface
public interface UncheckedBlock {
    void call() throws Exception;
}
