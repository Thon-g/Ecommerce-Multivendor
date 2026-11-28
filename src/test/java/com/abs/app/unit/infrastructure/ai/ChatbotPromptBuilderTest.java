package com.abs.app.unit.infrastructure.ai;

import com.abs.app.infrastructure.ai.ChatbotPromptBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChatbotPromptBuilderTest {

    private ChatbotPromptBuilder promptBuilder;

    @BeforeEach
    void setUp() {
        promptBuilder = new ChatbotPromptBuilder();
    }

    @Test
    @DisplayName("Should load prompt template from resource file and inject RAG context")
    void shouldBuildSystemPromptFromResource() {
        String ragContext = "[1] SẢN PHẨM: (ID: prod-1) Áo sơ mi trắng Oxford";

        String systemPrompt = promptBuilder.buildSystemPrompt(ragContext);

        assertNotNull(systemPrompt);
        assertTrue(systemPrompt.contains("<system_instructions>"));
        assertTrue(systemPrompt.contains("</system_instructions>"));
        assertTrue(systemPrompt.contains("<rag_context>"));
        assertTrue(systemPrompt.contains(ragContext));
        assertTrue(systemPrompt.contains("</rag_context>"));
        assertFalse(systemPrompt.contains("{rag_context}"));
    }

    @Test
    @DisplayName("Should gracefully handle null or blank RAG context")
    void shouldHandleNullContext() {
        String systemPrompt = promptBuilder.buildSystemPrompt(null);

        assertNotNull(systemPrompt);
        assertTrue(systemPrompt.contains("Không có thông tin bổ sung từ kho tri thức."));
    }
}
