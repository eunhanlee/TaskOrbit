package com.eunhanlee.taskorbit.dto;

import com.eunhanlee.taskorbit.entity.enums.TaskSize;
import com.eunhanlee.taskorbit.entity.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskRequest {
    private String title;
    private String category;
    private TaskSize size;
    private TaskStatus status;
    private LocalDate dueDate;
}


