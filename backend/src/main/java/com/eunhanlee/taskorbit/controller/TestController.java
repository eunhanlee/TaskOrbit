package com.eunhanlee.taskorbit.controller;

import com.eunhanlee.taskorbit.entity.Task;
import com.eunhanlee.taskorbit.entity.enums.TaskSize;
import com.eunhanlee.taskorbit.entity.enums.TaskStatus;
import com.eunhanlee.taskorbit.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TestController {

    private final TaskService taskService;

    // 테스트 데이터 생성 (GET - 브라우저 테스트용)
    @GetMapping("/seed")
    public ResponseEntity<Map<String, String>> createTestData() {
        // Today 탭용 작업들
        Task todayTask1 = Task.builder()
                .title("오늘 할 일 1")
                .category("개발")
                .size(TaskSize.UNDER_30_MIN)
                .status(TaskStatus.ONGOING)
                .workDate(LocalDate.now())
                .scheduleDate(LocalDate.now())
                .build();
        taskService.createTask(todayTask1);

        Task todayTask2 = Task.builder()
                .title("오늘 할 일 2")
                .category("회의")
                .size(TaskSize.OVER_1_HOUR)
                .status(TaskStatus.ONGOING)
                .workDate(LocalDate.now())
                .scheduleDate(LocalDate.now())
                .build();
        taskService.createTask(todayTask2);

        // Later 탭용 작업들
        Task laterTask1 = Task.builder()
                .title("나중에 할 일 1")
                .category("개발")
                .size(TaskSize.UNDER_10_MIN)
                .status(TaskStatus.ONGOING)
                .workDate(LocalDate.now())
                .scheduleDate(LocalDate.now().plusDays(3))
                .build();
        taskService.createTask(laterTask1);

        // Done 탭용 작업
        Task doneTask = Task.builder()
                .title("완료된 작업")
                .category("테스트")
                .size(TaskSize.UNDER_30_MIN)
                .status(TaskStatus.DONE)
                .workDate(LocalDate.now().minusDays(1))
                .scheduleDate(LocalDate.now().minusDays(1))
                .build();
        taskService.completeTask(taskService.createTask(doneTask).getId());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Test data created successfully");
        response.put("count", "4 tasks created");
        
        return ResponseEntity.ok(response);
    }
}

