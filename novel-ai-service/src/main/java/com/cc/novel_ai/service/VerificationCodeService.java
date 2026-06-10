package com.cc.novel_ai.service;

import com.cc.novel_ai.config.SmsConfig;
import com.cc.novel_ai.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 验证码服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationCodeService {

    private final SmsConfig smsConfig;
    private final SmsService smsService;

    /**
     * 验证码存储：手机号 -> 验证码数据
     */
    private final Map<String, CodeData> codeStorage = new ConcurrentHashMap<>();

    /**
     * 发送验证码
     *
     * @param phone 手机号
     */
    public void sendCode(String phone) {
        // 检查发送间隔
        CodeData existingData = codeStorage.get(phone);
        if (existingData != null) {
            long elapsed = System.currentTimeMillis() - existingData.getSendTime();
            if (elapsed < smsConfig.getSendInterval() * 1000) {
                long remaining = (smsConfig.getSendInterval() * 1000 - elapsed) / 1000;
                throw new BadRequestException("发送太频繁，请" + remaining + "秒后重试");
            }
        }

        // 生成 6 位随机验证码
        String code = generateCode();

        // 存储验证码
        CodeData codeData = new CodeData(code, System.currentTimeMillis());
        codeStorage.put(phone, codeData);

        // 发送短信
        smsService.sendVerificationCode(phone, code);

        log.info("Verification code sent to {}", phone);
    }

    /**
     * 验证验证码
     *
     * @param phone 手机号
     * @param code  验证码
     */
    public void verifyCode(String phone, String code) {
        CodeData codeData = codeStorage.get(phone);

        if (codeData == null) {
            throw new BadRequestException("验证码已过期或未发送");
        }

        // 检查验证码是否过期
        long elapsed = System.currentTimeMillis() - codeData.getSendTime();
        if (elapsed > smsConfig.getCodeExpiration() * 1000) {
            codeStorage.remove(phone);
            throw new BadRequestException("验证码已过期，请重新发送");
        }

        // 验证验证码
        if (!codeData.getCode().equals(code)) {
            throw new BadRequestException("验证码错误");
        }

        // 验证成功，删除验证码
        codeStorage.remove(phone);
        log.info("Verification code verified successfully for {}", phone);
    }

    /**
     * 生成 6 位随机验证码
     */
    private String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    /**
     * 验证码数据类
     */
    private static class CodeData {
        private final String code;
        private final long sendTime;

        public CodeData(String code, long sendTime) {
            this.code = code;
            this.sendTime = sendTime;
        }

        public String getCode() {
            return code;
        }

        public long getSendTime() {
            return sendTime;
        }
    }
}
