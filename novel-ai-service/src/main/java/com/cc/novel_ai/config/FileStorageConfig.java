package com.cc.novel_ai.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 文件存储配置
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.upload")
public class FileStorageConfig {

    /**
     * 上传文件存储路径
     */
    private String path = "./uploads/images/";

    /**
     * 访问文件的URL前缀
     */
    private String urlPrefix = "/files/images/";

    /**
     * 服务器基础地址（用于生成完整的文件访问URL）
     */
    private String baseUrl = "http://localhost:8080";
}
