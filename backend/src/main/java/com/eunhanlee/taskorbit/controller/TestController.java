package com.eunhanlee.taskorbit.controller;

import com.eunhanlee.taskorbit.entity.RecurringTaskSetting;
import com.eunhanlee.taskorbit.entity.Task;
import com.eunhanlee.taskorbit.entity.enums.RecurrenceType;
import com.eunhanlee.taskorbit.entity.enums.TaskSize;
import com.eunhanlee.taskorbit.entity.enums.TaskStatus;
import com.eunhanlee.taskorbit.service.RecurringTaskSettingService;
import com.eunhanlee.taskorbit.service.SchedulerService;
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
    private final SchedulerService schedulerService;
    private final RecurringTaskSettingService recurringTaskSettingService;

    // 테스트 데이터 생성 (GET - 브라우저 테스트용)
    @GetMapping("/seed")
    public ResponseEntity<Map<String, String>> createTestData() {
        // Today 탭용 작업들
        Task todayTask1 = Task.builder()
                .title("오늘 할 일 1")
                .category("개발")
                .size(TaskSize.UNDER_30_MIN)
                .status(TaskStatus.ONGOING)
                .dueDate(LocalDate.now())
                .build();
        taskService.createTask(todayTask1);

        Task todayTask2 = Task.builder()
                .title("오늘 할 일 2")
                .category("회의")
                .size(TaskSize.OVER_1_HOUR)
                .status(TaskStatus.ONGOING)
                .dueDate(LocalDate.now())
                .build();
        taskService.createTask(todayTask2);

        // Later 탭용 작업들
        Task laterTask1 = Task.builder()
                .title("나중에 할 일 1")
                .category("개발")
                .size(TaskSize.UNDER_10_MIN)
                .status(TaskStatus.ONGOING)
                .dueDate(LocalDate.now().plusDays(3))
                .build();
        taskService.createTask(laterTask1);

        // Done 탭용 작업
        Task doneTask = Task.builder()
                .title("완료된 작업")
                .category("테스트")
                .size(TaskSize.UNDER_30_MIN)
                .status(TaskStatus.DONE)
                .dueDate(LocalDate.now().minusDays(1))
                .build();
        taskService.completeTask(taskService.createTask(doneTask).getId());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Test data created successfully");
        response.put("count", "4 tasks created");
        
        return ResponseEntity.ok(response);
    }

    // 스케줄러 수동 실행 (테스트용)
    @PostMapping("/scheduler/run")
    public ResponseEntity<Map<String, String>> runScheduler() {
        schedulerService.dailyTaskScheduler();
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Scheduler executed successfully");
        
        return ResponseEntity.ok(response);
    }

    // 반복 작업 생성 테스트 (GET - 브라우저 테스트용)
    @GetMapping("/recurring/generate")
    public ResponseEntity<Map<String, String>> generateRecurringTasks() {
        recurringTaskSettingService.generateRecurringTasks();
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Recurring tasks generation completed");
        
        return ResponseEntity.ok(response);
    }

    // 반복 작업 설정 생성 테스트 (GET - 브라우저 테스트용)
    @GetMapping("/recurring/seed")
    public ResponseEntity<Map<String, String>> createRecurringTaskSettings() {
        // DAILY 반복 작업 설정
        RecurringTaskSetting dailySetting = RecurringTaskSetting.builder()
                .title("매일 운동하기")
                .category("건강")
                .size(TaskSize.UNDER_30_MIN)
                .recurrenceType(RecurrenceType.DAILY)
                .isActive(true)
                .build();
        recurringTaskSettingService.createSetting(dailySetting);

        // WEEKLY 반복 작업 설정
        RecurringTaskSetting weeklySetting = RecurringTaskSetting.builder()
                .title("주간 회의 준비")
                .category("업무")
                .size(TaskSize.UNDER_30_MIN)
                .recurrenceType(RecurrenceType.WEEKLY)
                .isActive(true)
                .build();
        recurringTaskSettingService.createSetting(weeklySetting);

        // MONTHLY 반복 작업 설정
        RecurringTaskSetting monthlySetting = RecurringTaskSetting.builder()
                .title("월간 리포트 작성")
                .category("업무")
                .size(TaskSize.OVER_1_HOUR)
                .recurrenceType(RecurrenceType.MONTHLY)
                .isActive(true)
                .build();
        recurringTaskSettingService.createSetting(monthlySetting);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Recurring task settings created successfully");
        response.put("count", "3 settings created");
        
        return ResponseEntity.ok(response);
    }
}

