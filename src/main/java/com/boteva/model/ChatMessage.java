package com.boteva.model;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {
    // Role can be: "user", "assistant", "system"
    private String role;
    private String message;
    private LocalDateTime timestamp;
}