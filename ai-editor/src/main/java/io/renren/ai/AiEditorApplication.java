package io.renren.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.mybatis.spring.annotation.MapperScan;

/**
 * AI Editor 后端启动类
 */
@SpringBootApplication
@ComponentScan(basePackages = {"io.renren.ai", "io.renren.project"})
@MapperScan(basePackages = {"io.renren.ai.dao", "io.renren.project.dao"})
public class AiEditorApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiEditorApplication.class, args);
    }
}
