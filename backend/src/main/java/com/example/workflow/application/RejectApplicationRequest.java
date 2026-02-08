package com.example.workflow.application;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RejectApplicationRequest(
        @Size(max = 500, message = "コメントは500文字以内で入力してください")
        String comment,

        @NotBlank(message = "却下理由は必須です")
        @Size(max = 500, message = "却下理由は500文字以内で入力してください")
        String rejectionReason
) {
}
