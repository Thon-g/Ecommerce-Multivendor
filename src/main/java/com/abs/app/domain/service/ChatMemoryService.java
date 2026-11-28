package com.abs.app.domain.service;

import com.abs.app.domain.model.GuestChatMessage;

import java.util.List;

public interface ChatMemoryService {

    void saveMessage(String guestSessionId, GuestChatMessage message);

    List<GuestChatMessage> getRecentMessages(String guestSessionId, int limit);

    List<GuestChatMessage> getAllMessages(String guestSessionId);

    void clearSession(String guestSessionId);
}
