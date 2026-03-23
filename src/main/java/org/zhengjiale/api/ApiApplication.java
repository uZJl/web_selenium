package org.zhengjiale.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 测试执行服务启动类
 *
 * 启动后可通过 HTTP API 触发测试执行:
 *
 * 启动命令: java -jar web_selenium.jar --server.port=8080
 *
 * API 调用示例:
 * curl -X POST http://localhost:8080/api/test/execute \
 *   -H "Content-Type: application/json" \
 *   -d '{"testClass":"BingSearchTest","browser":"chrome"}'
 */
@SpringBootApplication
@EnableAsync
public class ApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }
}
