package com.abs.app.infrastructure.ai;

import com.abs.app.domain.service.ChatPromptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class ChatbotPromptBuilder implements ChatPromptService {

    private static final String DEFAULT_PROMPT_PATH = "prompts/chatbot-system-prompt.st";
    private static final String EMPTY_CONTEXT_FALLBACK = "Không có thông tin bổ sung từ kho tri thức.";

    private final String systemPromptTemplate;

    public ChatbotPromptBuilder() {
        this(new ClassPathResource(DEFAULT_PROMPT_PATH));
    }

    @Autowired
    public ChatbotPromptBuilder(@Value("classpath:prompts/chatbot-system-prompt.st") Resource systemPromptResource) {
        this.systemPromptTemplate = loadTemplate(systemPromptResource);
    }

    @Override
    public String buildSystemPrompt(String ragContext) {
        String resolvedContext = (ragContext != null && !ragContext.isBlank())
                ? ragContext.trim()
                : EMPTY_CONTEXT_FALLBACK;

        return systemPromptTemplate.replace("{rag_context}", resolvedContext).trim();
    }

    private String loadTemplate(Resource resource) {
        try (InputStream inputStream = resource.getInputStream()) {
            return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load prompt template from resource: " + resource.getDescription(), e);
        }
    }
}
