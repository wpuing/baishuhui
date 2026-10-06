package com.baishuhui.interfaces.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

/**
 * 本地开发时把 /uploads/** 映射到磁盘目录。
 *
 * @author wei yz
 */
@Configuration
public class UploadWebConfig implements WebMvcConfigurer {

    @Value("${bsh.upload.dir:./runtime/uploads}")
    private String uploadDir;

    /**
     * 静态资源：相对路径 /uploads/supply/... 对应上传目录下文件。
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path root = Path.of(uploadDir).toAbsolutePath().normalize();
        String location = root.toUri().toString();
        if (!location.endsWith("/")) {
            location = location + "/";
        }
        registry.addResourceHandler("/uploads/**").addResourceLocations(location);
    }
}
