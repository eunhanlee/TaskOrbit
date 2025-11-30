package com.eunhanlee.taskorbit.dto;

import com.eunhanlee.taskorbit.entity.TaskLog;
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
public class TaskLogResponse {
    private Long id;
    private Long taskId;
    private LocalDate date;
    private String content;
    private String nextAction;
    private String historyLog;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TaskLogResponse from(TaskLog taskLog) {
        return TaskLogResponse.builder()
                .id(taskLog.getId())
                .taskId(taskLog.getTask().getId())
                .date(taskLog.getDate())
                .content(taskLog.getContent())
                .nextAction(taskLog.getNextAction())
                .historyLog(taskLog.getHistoryLog())
                .createdAt(taskLog.getCreatedAt())
                .updatedAt(taskLog.getUpdatedAt())
                .build();
    }
}


