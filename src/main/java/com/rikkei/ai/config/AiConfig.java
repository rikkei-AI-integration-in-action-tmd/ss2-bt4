package com.rikkei.ai.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
public class AiConfig {

    @Bean
    @Profile("local")
    @Primary
    @ConditionalOnBean(OllamaChatModel.class)
    public ChatModel localPrimaryChatModel(OllamaChatModel ollamaChatModel) {
        return ollamaChatModel;
    }

    @Bean
    @Profile("cloud")
    @Primary
    @ConditionalOnBean(OpenAiChatModel.class)
    public ChatModel cloudPrimaryChatModel(OpenAiChatModel openAiChatModel) {
        return openAiChatModel;
    }
}
