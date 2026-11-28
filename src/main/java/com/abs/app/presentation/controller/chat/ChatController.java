package com.abs.app.presentation.controller.chat;

import com.abs.app.application.chat.command.DeleteChatSessionCommand;
import com.abs.app.application.chat.command.DeleteChatSessionCommandHandler;
import com.abs.app.application.chat.command.MigrateGuestChatCommand;
import com.abs.app.application.chat.command.MigrateGuestChatCommandHandler;
import com.abs.app.application.chat.dto.*;
import com.abs.app.application.chat.query.*;
import com.abs.app.common.constant.ChatConstant;
import com.abs.app.common.response.ApiResponse;
import com.abs.app.common.response.PageResponse;
import com.abs.app.infrastructure.security.CustomUserPrincipal;
import com.abs.app.infrastructure.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final AskChatbotQueryHandler askChatbotQueryHandler;
    private final GetUserChatSessionsQueryHandler getUserChatSessionsQueryHandler;
    private final GetChatHistoryQueryHandler getChatHistoryQueryHandler;
    private final DeleteChatSessionCommandHandler deleteChatSessionCommandHandler;
    private final MigrateGuestChatCommandHandler migrateGuestChatCommandHandler;

    @PostMapping
    public ResponseEntity<ApiResponse<ChatResponseDto>> askChatbot(@Valid @RequestBody ChatRequestDto request) {
        CustomUserPrincipal principal = SecurityUtils.getCurrentUserPrincipal();
        String userId = principal != null ? principal.getUserId() : null;

        AskChatbotQuery query = AskChatbotQuery.builder()
                .userId(userId)
                .conversationId(request.getConversationId())
                .guestSessionId(request.getGuestSessionId())
                .message(request.getMessage())
                .build();

        ChatResponseDto response = askChatbotQueryHandler.handle(query);
        return ResponseEntity.ok(new ApiResponse<>(true, ChatConstant.CHAT_SUCCESS, response));
    }

    @GetMapping("/sessions")
    public ResponseEntity<ApiResponse<List<ChatSessionSummaryDto>>> getSessions() {
        String userId = SecurityUtils.getCurrentUserId();
        List<ChatSessionSummaryDto> sessions = getUserChatSessionsQueryHandler.handle(new GetUserChatSessionsQuery(userId));
        return ResponseEntity.ok(new ApiResponse<>(true, ChatConstant.CHAT_SESSIONS_FETCHED_SUCCESS, sessions));
    }

    @GetMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<ApiResponse<PageResponse<ChatMessageDto>>> getSessionMessages(
            @PathVariable String sessionId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        String userId = SecurityUtils.getCurrentUserId();
        PageResponse<ChatMessageDto> messages = getChatHistoryQueryHandler.handle(
                new GetChatHistoryQuery(sessionId, userId, page, limit)
        );
        return ResponseEntity.ok(new ApiResponse<>(true, ChatConstant.CHAT_HISTORY_FETCHED_SUCCESS, messages));
    }

    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<ApiResponse<Void>> deleteSession(@PathVariable String sessionId) {
        String userId = SecurityUtils.getCurrentUserId();
        deleteChatSessionCommandHandler.handle(new DeleteChatSessionCommand(sessionId, userId));
        return ResponseEntity.ok(new ApiResponse<>(true, ChatConstant.CHAT_SESSION_DELETED_SUCCESS, null));
    }

    @PostMapping("/migrate")
    public ResponseEntity<ApiResponse<ChatSessionSummaryDto>> migrateGuestSession(
            @Valid @RequestBody GuestMigrationRequestDto request) {
        String userId = SecurityUtils.getCurrentUserId();
        ChatSessionSummaryDto summary = migrateGuestChatCommandHandler.handle(
                new MigrateGuestChatCommand(userId, request.getGuestSessionId())
        );
        String message = summary != null ? ChatConstant.CHAT_MIGRATION_SUCCESS : ChatConstant.GUEST_SESSION_NOT_FOUND;
        return ResponseEntity.ok(new ApiResponse<>(true, message, summary));
    }
}
