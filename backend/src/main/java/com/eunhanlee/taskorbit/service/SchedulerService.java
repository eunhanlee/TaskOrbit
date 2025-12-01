package com.eunhanlee.taskorbit.service;

import com.eunhanlee.taskorbit.entity.Task;
import com.eunhanlee.taskorbit.entity.TaskCompletionRecord;
import com.eunhanlee.taskorbit.entity.enums.TaskStatus;
import com.eunhanlee.taskorbit.repository.TaskCompletionRecordRepository;
import com.eunhanlee.taskorbit.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {

    private final TaskRepository taskRepository;
    private final TaskCompletionRecordRepository completionRecordRepository;
    private final TaskService taskService;
    private final RecurringTaskSettingService recurringTaskSettingService;

    /**
     * 매일 3시 AM에 실행되는 스케줄러
     * 1. Waiting → Ongoing 변경
     * 2. Done → Record 이동 (TaskCompletionRecord 확인 및 생성)
     * 3. 미완료 작업의 schedule_date 롤오버
     * 4. 반복 작업 자동 생성
     */
    @Scheduled(cron = "0 0 3 * * ?") // 매일 3시 AM
    @Transactional
    public void dailyTaskScheduler() {
        log.info("Starting daily task scheduler at 3:00 AM");
        
        try {
            // 1. Waiting → Ongoing 변경
            activateWaitingTasks();
            
            // 2. Done → Record 이동 (TaskCompletionRecord 확인 및 생성)
            moveDoneToRecord();
            
            // 3. 미완료 작업의 schedule_date 롤오버
            rolloverIncompleteTasks();
            
            // 4. 반복 작업 자동 생성
            generateRecurringTasks();
            
            log.info("Daily task scheduler completed successfully");
        } catch (Exception e) {
            log.error("Error in daily task scheduler", e);
        }
    }

    /**
     * Waiting 상태인 작업들을 Ongoing으로 변경
     */
    @Transactional
    public void activateWaitingTasks() {
        List<Task> waitingTasks = taskRepository.findByStatus(TaskStatus.WAITING);
        
        for (Task task : waitingTasks) {
            task.setStatus(TaskStatus.ONGOING);
            taskRepository.save(task);
        }
        
        log.info("Activated {} waiting tasks to ongoing", waitingTasks.size());
    }

    /**
     * Done 상태인 작업들을 Record로 이동
     * TaskCompletionRecord가 없는 경우 생성
     */
    @Transactional
    public void moveDoneToRecord() {
        List<Task> doneTasks = taskRepository.findByStatus(TaskStatus.DONE);
        LocalDate yesterday = LocalDate.now().minusDays(1);
        int movedCount = 0;
        
        for (Task task : doneTasks) {
            // TaskCompletionRecord가 없는 경우 생성
            if (!completionRecordRepository.existsByTaskId(task.getId())) {
                TaskCompletionRecord record = TaskCompletionRecord.builder()
                        .task(task)
                        .completedDate(yesterday) // 어제 완료된 것으로 간주
                        .build();
                completionRecordRepository.save(record);
                movedCount++;
            }
        }
        
        log.info("Moved {} done tasks to record", movedCount);
    }

    /**
     * 미완료 작업의 due_date를 다음날로 롤오버
     */
    @Transactional
    public void rolloverIncompleteTasks() {
        LocalDate today = LocalDate.now();
        List<Task> incompleteTasks = taskRepository.findByDueDateLessThanEqual(today)
                .stream()
                .filter(task -> task.getStatus() != TaskStatus.DONE)
                .toList();
        
        for (Task task : incompleteTasks) {
            task.setDueDate(today.plusDays(1));
            taskRepository.save(task);
        }
        
        log.info("Rolled over {} incomplete tasks", incompleteTasks.size());
    }

    /**
     * 반복 작업 자동 생성
     */
    @Transactional
    public void generateRecurringTasks() {
        recurringTaskSettingService.generateRecurringTasks();
        log.info("Recurring tasks generation completed");
    }
}

