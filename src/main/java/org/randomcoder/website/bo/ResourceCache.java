package org.randomcoder.website.bo;

import java.util.function.Supplier;

public interface ResourceCache {

    CachedResource loadResource(String path, Supplier<byte[]> supplier);

}
