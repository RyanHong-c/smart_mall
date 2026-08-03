package com.smartmall.utils;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BloomFilterService {

    private final RedissonClient redissonClient;

    private static final String PRODUCT_BLOOM_KEY = "bloom:product";

    private static final long EXPECTED_INSERTIONS = 100_000;

    private static final double FALSE_PROBABILITY = 0.01;

    private RBloomFilter<Long> productBloomFilter;

    @PostConstruct
    public void init() {
        productBloomFilter = redissonClient.getBloomFilter(PRODUCT_BLOOM_KEY);
        if (!productBloomFilter.isExists()) {
            productBloomFilter.tryInit(EXPECTED_INSERTIONS, FALSE_PROBABILITY);
        }
    }

    public boolean mightContain(Long productId) {
        return productBloomFilter.contains(productId);
    }

    public void add(Long productId) {
        productBloomFilter.add(productId);
    }
}