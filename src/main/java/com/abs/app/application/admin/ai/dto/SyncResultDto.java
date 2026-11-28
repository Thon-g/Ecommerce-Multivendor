package com.abs.app.application.admin.ai.dto;

import com.abs.app.domain.model.AiSyncResult;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SyncResultDto {
    private boolean success;
    private String message;
    private int totalProductsSynced;
    private int totalCategoriesSynced;
    private long durationMillis;

    public static SyncResultDto from(AiSyncResult result) {
        if (result == null) {
            return null;
        }
        return SyncResultDto.builder()
                .success(result.isSuccess())
                .message(result.getMessage())
                .totalProductsSynced(result.getTotalProductsSynced())
                .totalCategoriesSynced(result.getTotalCategoriesSynced())
                .durationMillis(result.getDurationMillis())
                .build();
    }
}
