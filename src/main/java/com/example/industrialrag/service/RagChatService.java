package com.example.industrialrag.service;

import org.springframework.stereotype.Service;

/**
 * 作者：xiepk
 * 简单的 RAG 聊天服务，封装对 IndustrialAssistant 的调用。
 */
@Service
public class RagChatService {

    private final IndustrialAssistant assistant;

    public RagChatService(IndustrialAssistant assistant) {
        this.assistant = assistant;
    }

    /**
     * 根据用户问题调用 RAG 助手，返回增强后的专业回答。
     */
    public String chat(String question) {
        return assistant.answer(question);
    }
}

