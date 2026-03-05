package com.example.industrialrag;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 作者：xiepk
 * 工业设备故障诊断聊天机器人 Demo
 * 说明：
 * - 基于 Spring Boot 3.x + LangChain4j（含 Easy RAG）
 * - LLM 使用本地 Ollama（例如 llama3.1:8b）
 *
 * 如何运行：
 * 1) 确保本地已安装并运行 Ollama：`ollama serve`
 * 2) 确保已拉取模型：`ollama pull llama3.1:8b` 与 `ollama pull all-minilm`
 * 3) 运行：`mvn spring-boot:run`
 *
 * 如何测试：
 * 使用 Postman/HTTP 工具向 `POST http://localhost:8080/chat` 发送 JSON：
 * {"question": "电机为什么会过热？"}
 */
@SpringBootApplication
public class IndustrialRagApplication {

    public static void main(String[] args) {
        SpringApplication.run(IndustrialRagApplication.class, args);
    }
}

