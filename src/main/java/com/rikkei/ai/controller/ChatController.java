package com.rikkei.ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel).build();
    }

    @GetMapping
    public Map<String, Object> chat(@RequestParam(defaultValue = "Xin chao! Ban la ai?") String prompt) {
        try {
            String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
            return Map.of(
                    "status", "success",
                    "prompt", prompt,
                    "response", response != null ? response : ""
            );
        } catch (Exception e) {
            return Map.of(
                    "status", "error",
                    "prompt", prompt,
                    "error", e.getMessage() != null ? e.getMessage() : "Unknown error"
            );
        }
    }
}
