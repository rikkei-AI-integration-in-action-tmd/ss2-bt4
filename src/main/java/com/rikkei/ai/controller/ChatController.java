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
    public Map<String, String> chat(@RequestParam(defaultValue = "Xin chao! Ban la model nao?") String prompt) {
        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();
        return Map.of("prompt", prompt, "response", response != null ? response : "");
    }
}
