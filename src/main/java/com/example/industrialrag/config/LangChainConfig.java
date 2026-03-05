package com.example.industrialrag.config;

import com.example.industrialrag.service.IndustrialAssistant;
import com.example.industrialrag.service.IndustrialManualData;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

import static dev.langchain4j.data.segment.TextSegment.from;
import static dev.langchain4j.internal.Utils.isNullOrEmpty;

/**
 * 作者：xiepk
 * LangChain4j + Ollama + Easy RAG 配置
 * - Chat 模型：OllamaChatModel（如 llama3.1:8b）
 * - Embedding 模型：OllamaEmbeddingModel（如 all-minilm）
 * - 向量存储：InMemoryEmbeddingStore（内存）
 * - 内容检索：EmbeddingStoreContentRetriever（基于向量检索）
 * - 对话记忆：MessageWindowChatMemory（保留最近若干轮对话）
 * - EasyRag：演示集成，便于后续扩展
 */
@Configuration
public class LangChainConfig {

    @Value("${ollama.base-url:http://localhost:11434}")
    private String ollamaBaseUrl;

    @Value("${ollama.chat-model:llama3.1:8b}")
    private String chatModelName;

    @Value("${ollama.embedding-model:all-minilm}")
    private String embeddingModelName;

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return OllamaChatModel.builder()
                .baseUrl(ollamaBaseUrl)
                .modelName(chatModelName)
                .temperature(0.2)
                .build();
    }

    @Bean
    public OllamaEmbeddingModel embeddingModel() {
        return OllamaEmbeddingModel.builder()
                .baseUrl(ollamaBaseUrl)
                .modelName(embeddingModelName)
                .build();
    }

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        return new InMemoryEmbeddingStore<>();
    }


    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .maxMessages(10)
                .build();
    }

    @Bean
    public IndustrialAssistant industrialAssistant(ChatLanguageModel chatModel,
                                                   EmbeddingStore<TextSegment> embeddingStore,
                                                   OllamaEmbeddingModel embeddingModel,
                                                   ChatMemory memory) {
        return AiServices.builder(IndustrialAssistant.class)
                .chatLanguageModel(chatModel)
                .chatMemory(memory)
                .build();
    }



    /**
     * 将模拟"工业设备维护手册"文本切分并写入向量库。
     * 为了便于回答中引用编号，这里在文本前加入"手册第 X 条："。
     */
    @PostConstruct
    public void ingestManuals() {
        EmbeddingStore<TextSegment> store = embeddingStore();
        OllamaEmbeddingModel embModel = embeddingModel();
    
        List<String> sections = IndustrialManualData.manualSections();
        int idx = 1;
        for (String section : sections) {
            String titled = "手册第" + idx + "条：" + section;
            List<TextSegment> segments = chunkAsSegments(titled, 300);
            for (TextSegment seg : segments) {
                dev.langchain4j.model.output.Response<Embedding> response = embModel.embed(seg.text());
                Embedding embedding = response.content();
                store.add(embedding, seg);
            }
            idx++;
        }
    }

    /**
     * 简单按句子与长度进行分块。
     */
    private List<TextSegment> chunkAsSegments(String text, int chunkSize) {
        List<TextSegment> segments = new ArrayList<>();
        String[] sentences = text.split("(?<=[。！？!?；;。])");
        StringBuilder sb = new StringBuilder();
        for (String s : sentences) {
            if (sb.length() + s.length() > chunkSize) {
                if (sb.length() > 0) {
                    segments.add(from(sb.toString().trim()));
                    sb.setLength(0);
                }
            }
            sb.append(s);
        }
        if (sb.length() > 0) {
            segments.add(from(sb.toString().trim()));
        }
        return segments;
    }
}

