package com.abs.app.application.user.info.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserImageCommand {
    private String userId;
    private MultipartFile picture;
}
