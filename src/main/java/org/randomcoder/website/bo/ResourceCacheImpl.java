package org.randomcoder.website.bo;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.EntityTag;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

@Singleton
public class ResourceCacheImpl implements ResourceCache {

    private final Map<String, CachedResource> contentCache = new HashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private Date lastModified;

    @Inject
    AppInfoBusiness appInfoBusiness;

    @PostConstruct
    public void init() {
        lastModified = Date.from(appInfoBusiness.getBuildDate());
    }

    @Override
    public CachedResource loadResource(String path, Supplier<byte[]> supplier) {
        lock.readLock().lock();
        try {
            var result = contentCache.get(path);
            if (result != null) {
                return result;
            }
        } finally {
            lock.readLock().unlock();
        }

        var entity = supplier.get();
        if (entity == null) {
            return null;
        }

        var tag = new EntityTag(generateEtag(entity));
        var resource = new CachedResource(path, tag, lastModified, entity);

        lock.writeLock().lock();
        try {
            contentCache.put(path, resource);
        } finally {
            lock.writeLock().unlock();
        }

        return resource;
    }

    static String generateEtag(byte[] entity) {
        try {
            var digest = MessageDigest.getInstance("SHA256");
            digest.update(entity);
            return HexFormat.of().formatHex(digest.digest());
        } catch (NoSuchAlgorithmException e){
            throw new IllegalArgumentException("Unable to get SHA-256", e);
        }
    }

}
