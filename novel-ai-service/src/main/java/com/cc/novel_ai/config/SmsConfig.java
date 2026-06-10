package com.cc.novel_ai.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云短信配置
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "aliyun.sms")
public class SmsConfig {

    /**
     * AccessKey ID
     */
    private String accessKeyId;

    /**
     * AccessKey Secret
     */
    private String accessKeySecret;

    /**
     * 短信签名
     */
    private String signName;

    /**
     * 短信模板代码
     */
    private String templateCode;

    /**
     * 是否启用短信服务
     */
    private boolean enabled = false;

    /**
     * 验证码有效期（秒），默认 5 分钟
     */
    private int codeExpiration = 300;

    /**
     * 发送间隔（秒），默认 60 秒
     */
    private int sendInterval = 60;
}
