package com.eunhanlee.taskorbit.controller;

import com.eunhanlee.taskorbit.dto.TaskRequest;
import com.eunhanlee.taskorbit.dto.TaskResponse;
import com.eunhanlee.taskorbit.entity.Task;
import com.eunhanlee.taskorbit.entity.TaskLog;
import com.eunhanlee.taskorbit.service.TaskLogService;
import com.eunhanlee.taskorbit.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskService taskService;
    private final TaskLogService taskLogService;

    // Today 탭 조회
    @GetMapping("/today")
    public ResponseEntity<List<TaskResponse>> getTodayTasks() {
        try {
            List<Task> tasks = taskService.getTodayTasks();
            List<TaskResponse> responses = tasks.stream()
                    .map(task -> {
                        try {
                            String nextAction = taskLogService.getLatestLog(task.getId())
                                    .map(TaskLog::getNextAction)
                                    .orElse(null);
                            return TaskResponse.from(task, nextAction);
                        } catch (Exception e) {
                            // 로그 조회 실패 시 nextAction 없이 응답
                            return TaskResponse.from(task, null);
                        }
                    })
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("Error fetching today tasks", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Later 탭 조회
    @GetMapping("/later")
    public ResponseEntity<List<TaskResponse>> getLaterTasks() {
        List<Task> tasks = taskService.getLaterTasks();
        List<TaskResponse> responses = tasks.stream()
                .map(task -> {
                    String nextAction = taskLogService.getLatestLog(task.getId())
                            .map(TaskLog::getNextAction)
                            .orElse(null);
                    return TaskResponse.from(task, nextAction);
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // Done 탭 조회
    @GetMapping("/done")
    public ResponseEntity<List<TaskResponse>> getDoneTasks() {
        List<Task> tasks = taskService.getDoneTasks();
        List<TaskResponse> responses = tasks.stream()
                .map(task -> {
                    String nextAction = taskLogService.getLatestLog(task.getId())
                            .map(TaskLog::getNextAction)
                            .orElse(null);
                    return TaskResponse.from(task, nextAction);
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // Record 탭 조회
    @GetMapping("/record")
    public ResponseEntity<List<TaskResponse>> getRecordTasks() {
        List<Task> tasks = taskService.getRecordTasks();
        List<TaskResponse> responses = tasks.stream()
                .map(task -> {
                    String nextAction = taskLogService.getLatestLog(task.getId())
                            .map(TaskLog::getNextAction)
                            .orElse(null);
                    return TaskResponse.from(task, nextAction);
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // 작업 조회
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable Long id) {
        Task task = taskService.getTask(id);
        String nextAction = taskLogService.getLatestLog(id)
                .map(TaskLog::getNextAction)
                .orElse(null);
        return ResponseEntity.ok(TaskResponse.from(task, nextAction));
    }

    // 작업 생성
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody TaskRequest request) {
        Task task = Task.builder()
                .title(request.getTitle())
                .category(request.getCategory())
                .size(request.getSize())
                .status(request.getStatus())
                .dueDate(request.getDueDate())
                .build();
        
        Task created = taskService.createTask(task);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TaskResponse.from(created));
    }

    // 작업 수정
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @RequestBody TaskRequest request) {
        Task task = Task.builder()
                .title(request.getTitle())
                .category(request.getCategory())
                .size(request.getSize())
                .status(request.getStatus())
                .dueDate(request.getDueDate())
                .build();
        
        Task updated = taskService.updateTask(id, task);
        String nextAction = taskLogService.getLatestLog(id)
                .map(TaskLog::getNextAction)
                .orElse(null);
        return ResponseEntity.ok(TaskResponse.from(updated, nextAction));
    }

    // 작업 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    // 작업 완료
    @PostMapping("/{id}/complete")
    public ResponseEntity<TaskResponse> completeTask(@PathVariable Long id) {
        Task task = taskService.completeTask(id);
        return ResponseEntity.ok(TaskResponse.from(task));
    }

    // 작업 완료 취소 (Done → Ongoing)
    @PostMapping("/{id}/uncomplete")
    public ResponseEntity<TaskResponse> uncompleteTask(@PathVariable Long id) {
        Task task = taskService.uncompleteTask(id);
        String nextAction = taskLogService.getLatestLog(id)
                .map(TaskLog::getNextAction)
                .orElse(null);
        return ResponseEntity.ok(TaskResponse.from(task, nextAction));
    }

    // 작업을 Waiting 상태로 변경
    @PostMapping("/{id}/waiting")
    public ResponseEntity<TaskResponse> setTaskWaiting(@PathVariable Long id) {
        Task task = taskService.setTaskWaiting(id);
        String nextAction = taskLogService.getLatestLog(id)
                .map(TaskLog::getNextAction)
                .orElse(null);
        return ResponseEntity.ok(TaskResponse.from(task, nextAction));
    }

    // 작업을 Ongoing 상태로 활성화 (Waiting에서 활성화)
    @PostMapping("/{id}/activate")
    public ResponseEntity<TaskResponse> activateTask(@PathVariable Long id) {
        Task task = taskService.activateTask(id);
        String nextAction = taskLogService.getLatestLog(id)
                .map(TaskLog::getNextAction)
                .orElse(null);
        return ResponseEntity.ok(TaskResponse.from(task, nextAction));
    }

    // 카테고리별 조회
    @GetMapping("/category/{category}")
    public ResponseEntity<List<TaskResponse>> getTasksByCategory(@PathVariable String category) {
        List<Task> tasks = taskService.getTasksByCategory(category);
        List<TaskResponse> responses = tasks.stream()
                .map(task -> {
                    String nextAction = taskLogService.getLatestLog(task.getId())
                            .map(TaskLog::getNextAction)
                            .orElse(null);
                    return TaskResponse.from(task, nextAction);
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}

