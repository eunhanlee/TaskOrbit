package com.eunhanlee.taskorbit.controller;

import com.eunhanlee.taskorbit.dto.TaskLogRequest;
import com.eunhanlee.taskorbit.dto.TaskLogResponse;
import com.eunhanlee.taskorbit.entity.TaskLog;
import com.eunhanlee.taskorbit.service.TaskLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks/{taskId}/logs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TaskLogController {

    private final TaskLogService taskLogService;

    // 작업의 모든 로그 조회
    @GetMapping
    public ResponseEntity<List<TaskLogResponse>> getTaskLogs(@PathVariable Long taskId) {
        List<TaskLog> logs = taskLogService.getTaskLogs(taskId);
        List<TaskLogResponse> responses = logs.stream()
                .map(TaskLogResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // 최신 로그 조회 (Next Action)
    @GetMapping("/latest")
    public ResponseEntity<TaskLogResponse> getLatestLog(@PathVariable Long taskId) {
        return taskLogService.getLatestLog(taskId)
                .map(log -> ResponseEntity.ok(TaskLogResponse.from(log)))
                .orElse(ResponseEntity.notFound().build());
    }

    // 로그 생성
    @PostMapping
    public ResponseEntity<TaskLogResponse> createLog(
            @PathVariable Long taskId,
            @RequestBody TaskLogRequest request) {
        TaskLog taskLog = TaskLog.builder()
                .date(request.getDate())
                .content(request.getContent())
                .nextAction(request.getNextAction())
                .build();
        
        TaskLog created = taskLogService.createLog(taskId, taskLog);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TaskLogResponse.from(created));
    }

    // 로그 수정
    @PutMapping("/{logId}")
    public ResponseEntity<TaskLogResponse> updateLog(
            @PathVariable Long taskId,
            @PathVariable Long logId,
            @RequestBody TaskLogRequest request) {
        TaskLog taskLog = TaskLog.builder()
                .date(request.getDate())
                .content(request.getContent())
                .nextAction(request.getNextAction())
                .build();
        
        TaskLog updated = taskLogService.updateLog(logId, taskLog);
        return ResponseEntity.ok(TaskLogResponse.from(updated));
    }

    // 로그 삭제
    @DeleteMapping("/{logId}")
    public ResponseEntity<Void> deleteLog(
            @PathVariable Long taskId,
            @PathVariable Long logId) {
        taskLogService.deleteLog(logId);
        return ResponseEntity.noContent().build();
    }
}

