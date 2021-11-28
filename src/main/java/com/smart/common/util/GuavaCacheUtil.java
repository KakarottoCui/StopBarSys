package com.smart.common.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 本地缓存
 */
public class GuavaCacheUtil {

    public static final String TOKEN_PREFIX = "token_";

    /**
     * 过期时间12小时 使用了LRU算法
     */
    private static LoadingCache localCache =
            CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000).
                    expireAfterAccess(2, TimeUnit.HOURS).
                    build(new CacheLoader<Object, Object>() {
        @Override
        public Object load(Object o) {
            return "";
        }
    });

    /**
     * 设置缓存
     * @param key
     * @param value
     */
    public static void setKey(String key, String value) {
        key = TOKEN_PREFIX + key;
        localCache.put(key, value);
    }

    /**
     * 获取缓存
     * @param key
     * @return
     */
    public static String getKey(String key) {
        String value = null;
        try {
            key = TOKEN_PREFIX + key;
            value = (String) localCache.get(key);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return value;
    }
}
