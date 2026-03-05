package com.example.industrialrag.controller;

import com.example.industrialrag.service.RagChatService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * 作者：xiepk
 * 简单的聊天 REST 控制器：POST /chat
 */
@RestController
@RequestMapping("/chat")
public class ChatController {

    private final RagChatService chatService;

    public ChatController(RagChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ChatResponse chat(@RequestBody ChatRequest request) {
        String answer = chatService.chat(request.getQuestion());
        return new ChatResponse(answer);
    }

    /**
     * 请求 DTO
     */
    public static class ChatRequest {
        private String question;

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }
    }

    /**
     * 响应 DTO
     */
    public static class ChatResponse {
        private String answer;

        public ChatResponse() {
        }

        public ChatResponse(String answer) {
            this.answer = answer;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }
    }
}

