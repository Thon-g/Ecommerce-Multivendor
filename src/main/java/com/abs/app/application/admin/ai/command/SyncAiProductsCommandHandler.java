package com.abs.app.application.admin.ai.command;

import com.abs.app.application.admin.ai.dto.SyncResultDto;
import com.abs.app.domain.service.AiDataSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SyncAiProductsCommandHandler {

    private final AiDataSyncService aiDataSyncService;

    public SyncResultDto handle() {
        return SyncResultDto.from(aiDataSyncService.syncProducts());
    }
}
