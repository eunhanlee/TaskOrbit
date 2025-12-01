package com.eunhanlee.taskorbit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskLogRequest {
    private LocalDate date;
    private String content;
    private String nextAction;
}



