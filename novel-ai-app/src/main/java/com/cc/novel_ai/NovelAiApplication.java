package com.cc.novel_ai;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class NovelAiApplication {

    private static final long startTime = System.currentTimeMillis();

    public static void main(String[] args) {
        SpringApplication.run(NovelAiApplication.class, args);
    }

    @Bean
    public ApplicationRunner applicationRunner() {
        return new ApplicationRunner() {
            @Override
            public void run(ApplicationArguments args) {
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                System.out.println();
                System.out.println("========================================");
                System.out.println("    榴莲写作后台启动成功！");
                System.out.println("    用时：" + duration + "ms");
                System.out.println("========================================");
                System.out.println();
            }
        };
    }
}
