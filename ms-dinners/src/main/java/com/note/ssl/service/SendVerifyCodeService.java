package com.note.ssl.service;

import cn.hutool.core.util.RandomUtil;
import com.note.ssl.commons.constant.RedisKeyConstant;
import com.note.ssl.commons.utils.AssertUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @Author: SongShengLin
 * @Date: 2022/10/14 14:52
 * @Describe: 发送验证码
 */
@Service
public class SendVerifyCodeService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    /**
     * 发送验证码
     */
    public void send(String phone) {
        // 非空判断
        AssertUtil.isNotEmpty(phone);
        // 是否已经发送过
        if (!checkSendPhone(phone)) {
            // 发送过：直接返回
            return;
        }
        // 没有发送过：生成6位验证码
        String code = RandomUtil.randomNumbers(6);
        // 调用短信服务发送短信
        // 假设已经发送过短信了
        // 发送成功，code保存进redis，设置过期时间60s
        String key = RedisKeyConstant.verify_code.getKey() + phone;
        redisTemplate.opsForValue().set(key, code, 60, TimeUnit.SECONDS);
    }

    private boolean checkSendPhone(String phone) {
        String key = RedisKeyConstant.verify_code.getKey() + phone;
        String value = (String) redisTemplate.opsForValue().get(key);
        return StringUtils.isBlank(value);
    }

    public String getCodeByPhone(String phone) {
        String key = RedisKeyConstant.verify_code.getKey() + phone;
        return (String) redisTemplate.opsForValue().get(key);
    }
}
