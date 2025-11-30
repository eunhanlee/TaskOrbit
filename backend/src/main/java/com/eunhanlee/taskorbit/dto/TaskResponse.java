package com.eunhanlee.taskorbit.dto;

import com.eunhanlee.taskorbit.entity.Task;
import com.eunhanlee.taskorbit.entity.enums.TaskSize;
import com.eunhanlee.taskorbit.entity.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponse {
    private Long id;
    private String title;
    private String category;
    private TaskSize size;
    private TaskStatus status;
    private LocalDate workDate;
    private LocalDate scheduleDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String nextAction; // 최신 로그의 nextAction

    public static TaskResponse from(Task task, String nextAction) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .category(task.getCategory())
                .size(task.getSize())
                .status(task.getStatus())
                .workDate(task.getWorkDate())
                .scheduleDate(task.getScheduleDate())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .nextAction(nextAction)
                .build();
    }

    public static TaskResponse from(Task task) {
        return from(task, null);
    }
}


