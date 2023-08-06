package com.spring.familymoments.domain.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;
    public void setValues(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }
    @Transactional
    public void setValuesWithTimeout(String key, String value, long timeout) { // 만료 시간을 설정해서 자동 삭제 가능
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.MILLISECONDS);
    }
    public String getValues(String key) {
        return redisTemplate.opsForValue().get(key);
    }
    public void deleteValues(String key) {
        redisTemplate.delete(key);
    }
}
