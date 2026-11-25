package com.abs.app.domain.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AiMessage {
    private String role;
    private String content;
}
