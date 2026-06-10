package com.cc.novel_ai.service;

import com.cc.novel_ai.config.FileStorageConfig;
import com.cc.novel_ai.exception.FileStorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件存储服务
 */
@Slf4j
@Service
public class FileStorageService {

    private final Path fileStorageLocation;
    private final FileStorageConfig config;

    /**
     * 允许的图片类型
     */
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp"
    );

    /**
     * 最大文件大小 (5MB)
     */
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    public FileStorageService(FileStorageConfig config) {
        this.config = config;
        this.fileStorageLocation = Paths.get(config.getPath()).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    /**
     * 存储文件
     *
     * @param file 文件
     * @return 文件访问URL
     */
    public String storeFile(MultipartFile file) {
        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new FileStorageException("File type not allowed. Allowed types: JPEG, PNG, GIF, WEBP");
        }

        // 验证文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileStorageException("File size exceeds maximum limit of 5MB");
        }

        // 清理文件名
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        if (originalFilename.contains("..")) {
            throw new FileStorageException("Filename contains invalid path sequence " + originalFilename);
        }

        try {
            // 生成唯一文件名
            String fileExtension = getFileExtension(originalFilename);
            String newFilename = UUID.randomUUID().toString() + fileExtension;

            // 创建日期子目录
            LocalDate today = LocalDate.now();
            String datePath = today.format(DateTimeFormatter.ofPattern("yyyy/MM"));
            Path targetLocation = this.fileStorageLocation.resolve(datePath).resolve(newFilename);

            // 创建目录
            Files.createDirectories(targetLocation.getParent());

            // 复制文件
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // 返回访问URL
            String accessUrl = config.getUrlPrefix() + datePath + "/" + newFilename;
            log.info("File stored successfully: {}", accessUrl);
            return accessUrl;

        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + originalFilename + ". Please try again!", ex);
        }
    }

    /**
     * 加载文件
     *
     * @param filename 文件名（相对URL）
     * @return 文件资源
     */
    public Resource loadFileAsResource(String filename) {
        try {
            // 清理路径，防止目录遍历攻击
            String cleanPath = filename.replace("\\", "/").replaceFirst("^/files/images/", "");
            Path filePath = this.fileStorageLocation.resolve(cleanPath).normalize();
            Path normalizedPath = filePath.normalize();

            // 验证路径安全性
            if (!normalizedPath.startsWith(this.fileStorageLocation)) {
                throw new FileStorageException("Cannot access file outside of configured directory");
            }

            Resource resource = new UrlResource(normalizedPath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new FileStorageException("File not found " + filename);
            }
        } catch (MalformedURLException ex) {
            throw new FileStorageException("File not found " + filename, ex);
        }
    }

    /**
     * 删除文件
     *
     * @param fileUrl 文件URL
     */
    public void deleteFile(String fileUrl) {
        try {
            String cleanPath = fileUrl.replace("\\", "/").replaceFirst("^/files/images/", "");
            Path filePath = this.fileStorageLocation.resolve(cleanPath).normalize();
            Path normalizedPath = filePath.normalize();

            if (normalizedPath.startsWith(this.fileStorageLocation) && Files.exists(normalizedPath)) {
                Files.delete(normalizedPath);
                log.info("File deleted successfully: {}", fileUrl);
            }
        } catch (IOException ex) {
            log.error("Could not delete file: {}", fileUrl, ex);
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}
