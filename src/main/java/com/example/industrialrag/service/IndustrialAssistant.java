package com.example.industrialrag.service;

/**
 * 作者：xiepk
 * 通过 LangChain4j AiServices 创建的对话接口。
 * 方法参数即为用户问题，返回 RAG 增强后的答案。
 */
public interface IndustrialAssistant {

    String answer(String question);
}

