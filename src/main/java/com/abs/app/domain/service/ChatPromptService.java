package com.abs.app.domain.service;

public interface ChatPromptService {

    String buildSystemPrompt(String ragContext);
}
