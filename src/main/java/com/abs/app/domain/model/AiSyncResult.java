package com.abs.app.domain.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AiSyncResult {
    private boolean success;
    private String message;
    private int totalProductsSynced;
    private int totalCategoriesSynced;
    private long durationMillis;
}
