package com.example.workflow.application;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ApplicationUpdateRequest(
        @NotBlank(message = "現住所は必須です")
        @Size(max = 255, message = "現住所は255文字以内で入力してください")
        String currentAddress,

        @NotBlank(message = "新住所は必須です")
        @Size(max = 255, message = "新住所は255文字以内で入力してください")
        String newAddress,

        @NotBlank(message = "変更理由は必須です")
        @Size(max = 500, message = "変更理由は500文字以内で入力してください")
        String reason
) {
}
