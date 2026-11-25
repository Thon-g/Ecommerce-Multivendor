package com.abs.app.domain.service;

import com.abs.app.domain.model.AiChatResponse;
import com.abs.app.domain.model.AiMessage;

import java.util.List;

public interface AiChatService {
    AiChatResponse generateResponse(String systemPrompt, List<AiMessage> conversationHistory, String userMessage);
}
