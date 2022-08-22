package com.alten.booking.business.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.grid.jcache.JCacheProxyManager;
import org.springframework.stereotype.Service;

import javax.cache.Cache;
import java.time.Duration;
import java.util.function.Supplier;

@Service
public class RateLimiter {

    private final ProxyManager<String> buckets;

    public RateLimiter(Cache<String, byte[]> cache) {
        buckets = new JCacheProxyManager<>(cache);
    }

    public Bucket resolveBucket(String key) {
        Supplier<BucketConfiguration> configSupplier = getConfigSupplierForUser(key);
        return buckets.builder().build(key, configSupplier);
    }

    private Supplier<BucketConfiguration> getConfigSupplierForUser(String user) {
        Bandwidth bandwidth = "GUEST".equals(user)
                //1000 requests for any non logged user, focus on quality of service for users logged in.
                ? Bandwidth.classic(1000, Refill.intervally(1000, Duration.ofMinutes(1)))
                //10 requests per minute by logged user, enough to finish any booking flow in a minute.
                : Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1)));
        return () -> (BucketConfiguration.builder()
                .addLimit(bandwidth)
                .build());
    }
}