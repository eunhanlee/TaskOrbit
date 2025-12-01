package com.eunhanlee.taskorbit.service;

import com.eunhanlee.taskorbit.entity.Task;
import com.eunhanlee.taskorbit.entity.TaskCompletionRecord;
import com.eunhanlee.taskorbit.entity.enums.ActionType;
import com.eunhanlee.taskorbit.entity.enums.TaskStatus;
import com.eunhanlee.taskorbit.repository.TaskCompletionRecordRepository;
import com.eunhanlee.taskorbit.repository.TaskRepository;
import com.eunhanlee.taskorbit.util.TaskConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskCompletionRecordRepository completionRecordRepository;
    private final GlobalLogService globalLogService;

    // Today 탭: due_date <= today인 작업들
    public List<Task> getTodayTasks() {
        LocalDate today = LocalDate.now();
        return taskRepository.findTodayTasks(today);
    }

    // Later 탭: due_date > today인 작업들
    public List<Task> getLaterTasks() {
        LocalDate today = LocalDate.now();
        return taskRepository.findLaterTasks(today);
    }

    // Done 탭: status = DONE인 작업들
    public List<Task> getDoneTasks() {
        return taskRepository.findByStatusOrderByUpdatedAtDesc(TaskStatus.DONE);
    }

    // Record 탭: 완료 기록이 있는 작업들
    public List<Task> getRecordTasks() {
        // TaskCompletionRecord를 통해 완료된 작업들을 조회
        return completionRecordRepository.findAllByOrderByCompletedDateDesc()
                .stream()
                .map(TaskCompletionRecord::getTask)
                .distinct()
                .toList();
    }

    // 작업 생성
    @Transactional
    public Task createTask(Task task) {
        // 새 작업 생성 시 due_date를 오늘로 설정
        if (task.getDueDate() == null) {
            task.setDueDate(LocalDate.now());
        }
        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.ONGOING);
        }
        Task savedTask = taskRepository.save(task);
        
        // GlobalLog 기록
        globalLogService.createLog(
            "Task",
            savedTask.getId(),
            ActionType.CREATE,
            null,
            TaskConverter.taskToMap(savedTask)
        );
        
        return savedTask;
    }

    // 작업 조회
    public Task getTask(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
    }

    // 작업 수정
    @Transactional
    public Task updateTask(Long id, Task updatedTask) {
        Task task = getTask(id);
        Map<String, Object> oldData = TaskConverter.taskToMap(task);
        
        if (updatedTask.getTitle() != null) {
            task.setTitle(updatedTask.getTitle());
        }
        if (updatedTask.getCategory() != null) {
            task.setCategory(updatedTask.getCategory());
        }
        if (updatedTask.getSize() != null) {
            task.setSize(updatedTask.getSize());
        }
        if (updatedTask.getStatus() != null) {
            task.setStatus(updatedTask.getStatus());
        }
        if (updatedTask.getDueDate() != null) {
            task.setDueDate(updatedTask.getDueDate());
        }
        
        Task savedTask = taskRepository.save(task);
        
        // GlobalLog 기록
        globalLogService.createLog(
            "Task",
            savedTask.getId(),
            ActionType.UPDATE,
            oldData,
            TaskConverter.taskToMap(savedTask)
        );
        
        return savedTask;
    }

    // 작업 삭제
    @Transactional
    public void deleteTask(Long id) {
        Task task = getTask(id);
        Map<String, Object> oldData = TaskConverter.taskToMap(task);
        
        taskRepository.deleteById(id);
        
        // GlobalLog 기록
        globalLogService.createLog(
            "Task",
            id,
            ActionType.DELETE,
            oldData,
            null
        );
    }

    // 작업 완료 처리
    @Transactional
    public Task completeTask(Long id) {
        Task task = getTask(id);
        Map<String, Object> oldData = TaskConverter.taskToMap(task);
        
        task.setStatus(TaskStatus.DONE);
        
        // 완료 기록 생성
        TaskCompletionRecord record = TaskCompletionRecord.builder()
                .task(task)
                .completedDate(LocalDate.now())
                .build();
        completionRecordRepository.save(record);
        
        Task savedTask = taskRepository.save(task);
        
        // GlobalLog 기록
        globalLogService.createLog(
            "Task",
            savedTask.getId(),
            ActionType.UPDATE,
            oldData,
            TaskConverter.taskToMap(savedTask)
        );
        
        return savedTask;
    }

    // 작업 완료 취소 (Done → Ongoing)
    @Transactional
    public Task uncompleteTask(Long id) {
        Task task = getTask(id);
        Map<String, Object> oldData = TaskConverter.taskToMap(task);
        
        // 완료 기록 삭제
        completionRecordRepository.findByTaskId(id).ifPresent(completionRecordRepository::delete);
        
        // 상태를 Ongoing으로 변경
        task.setStatus(TaskStatus.ONGOING);
        
        Task savedTask = taskRepository.save(task);
        
        // GlobalLog 기록
        globalLogService.createLog(
            "Task",
            savedTask.getId(),
            ActionType.UPDATE,
            oldData,
            TaskConverter.taskToMap(savedTask)
        );
        
        return savedTask;
    }

    // 작업을 Waiting 상태로 변경
    @Transactional
    public Task setTaskWaiting(Long id) {
        Task task = getTask(id);
        Map<String, Object> oldData = TaskConverter.taskToMap(task);
        
        task.setStatus(TaskStatus.WAITING);
        Task savedTask = taskRepository.save(task);
        
        // GlobalLog 기록
        globalLogService.createLog(
            "Task",
            savedTask.getId(),
            ActionType.UPDATE,
            oldData,
            TaskConverter.taskToMap(savedTask)
        );
        
        return savedTask;
    }

    // 작업을 Ongoing 상태로 활성화 (Waiting에서 활성화)
    @Transactional
    public Task activateTask(Long id) {
        Task task = getTask(id);
        Map<String, Object> oldData = TaskConverter.taskToMap(task);
        
        task.setStatus(TaskStatus.ONGOING);
        Task savedTask = taskRepository.save(task);
        
        // GlobalLog 기록
        globalLogService.createLog(
            "Task",
            savedTask.getId(),
            ActionType.UPDATE,
            oldData,
            TaskConverter.taskToMap(savedTask)
        );
        
        return savedTask;
    }

    // 미완료 작업의 due_date를 다음날로 롤오버
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

    // Waiting 상태를 Ongoing으로 변경 (3시 AM에 실행)
    @Transactional
    public void activateWaitingTasks() {
        List<Task> waitingTasks = taskRepository.findByStatus(TaskStatus.WAITING);
        
        for (Task task : waitingTasks) {
            task.setStatus(TaskStatus.ONGOING);
            taskRepository.save(task);
        }
        
        log.info("Activated {} waiting tasks", waitingTasks.size());
    }

    // 카테고리별 조회
    public List<Task> getTasksByCategory(String category) {
        return taskRepository.findByCategory(category);
    }

    // 상태별 조회
    public List<Task> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }
}

