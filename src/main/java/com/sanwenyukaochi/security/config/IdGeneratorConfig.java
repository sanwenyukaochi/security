package com.sanwenyukaochi.security.config;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdGeneratorConfig {
    
    @Bean
    public Snowflake snowflake() {
        // 这里的 1 和 1 分别是 workerId 和 datacenterId
        // 在分布式环境中，这两个值需要不同服务器配置不同的值
        return IdUtil.getSnowflake(1, 1);
    }
}
