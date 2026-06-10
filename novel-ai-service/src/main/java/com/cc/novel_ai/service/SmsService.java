package com.cc.novel_ai.service;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.cc.novel_ai.config.SmsConfig;
import com.cc.novel_ai.exception.BadRequestException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 阿里云短信服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SmsService {

    private final SmsConfig smsConfig;
    private Client client;

    @PostConstruct
    public void init() {
        if (smsConfig.isEnabled()) {
            try {
                Config config = new Config()
                        .setAccessKeyId(smsConfig.getAccessKeyId())
                        .setAccessKeySecret(smsConfig.getAccessKeySecret())
                        .setEndpoint("dysmsapi.aliyuncs.com");
                this.client = new Client(config);
                log.info("Aliyun SMS client initialized successfully");
            } catch (Exception e) {
                log.error("Failed to initialize Aliyun SMS client", e);
            }
        } else {
            log.warn("Aliyun SMS service is disabled");
        }
    }

    /**
     * 发送短信验证码
     *
     * @param phone 手机号
     * @param code  验证码
     */
    public void sendVerificationCode(String phone, String code) {
        if (!smsConfig.isEnabled()) {
            log.info("SMS service is disabled, code for {} is: {}", phone, code);
            return;
        }

        if (client == null) {
            throw new BadRequestException("短信服务未初始化");
        }

        try {
            SendSmsRequest request = new SendSmsRequest()
                    .setPhoneNumbers(phone)
                    .setSignName(smsConfig.getSignName())
                    .setTemplateCode(smsConfig.getTemplateCode())
                    .setTemplateParam("{\"code\":\"" + code + "\"}");

            SendSmsResponse response = client.sendSms(request);

            if ("OK".equals(response.getBody().getCode())) {
                log.info("SMS sent successfully to {}", phone);
            } else {
                log.error("Failed to send SMS to {}: {}", phone, response.getBody().getMessage());
                throw new BadRequestException("短信发送失败: " + response.getBody().getMessage());
            }
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to send SMS to {}", phone, e);
            throw new BadRequestException("短信发送失败，请稍后重试");
        }
    }
}
