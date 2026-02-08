package com.example.workflow.application;

import jakarta.validation.constraints.Size;

public record ApplicationActionRequest(
        @Size(max = 500, message = "コメントは500文字以内で入力してください")
        String comment,

        @Size(max = 500, message = "却下理由は500文字以内で入力してください")
        String rejectionReason
) {
}
