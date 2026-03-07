package com.TreadX.file.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileRequestDTO {

    @NotBlank(message = "File path is required")
    private String filePath;

    private String originalFileName;
    private String storedFileName;
    private Long fileSize;
    private String mimeType;
}
