package com.eunhanlee.taskorbit.service;

import com.eunhanlee.taskorbit.entity.Task;
import com.eunhanlee.taskorbit.entity.TaskLog;
import com.eunhanlee.taskorbit.repository.TaskLogRepository;
import com.eunhanlee.taskorbit.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TaskLogService {

    private final TaskLogRepository taskLogRepository;
    private final TaskRepository taskRepository;

    // 특정 작업의 최신 로그 조회 (Next Action 표시용)
    public Optional<TaskLog> getLatestLog(Long taskId) {
        try {
            List<TaskLog> logs = taskLogRepository.findLatestLogsByTaskId(taskId);
            return logs.isEmpty() ? Optional.empty() : Optional.of(logs.get(0));
        } catch (Exception e) {
            log.error("Error fetching latest log for taskId: {}", taskId, e);
            return Optional.empty();
        }
    }

    // 특정 작업의 모든 로그 조회
    public List<TaskLog> getTaskLogs(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));
        return taskLogRepository.findByTaskOrderByDateDesc(task);
    }

    // 로그 생성
    @Transactional
    public TaskLog createLog(Long taskId, TaskLog taskLog) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));
        
        taskLog.setTask(task);
        if (taskLog.getDate() == null) {
            taskLog.setDate(LocalDate.now());
        }
        
        // History Log 업데이트: 기존 로그의 history_log에 새 내용 추가
        Optional<TaskLog> latestLog = getLatestLog(taskId);
        if (latestLog.isPresent() && latestLog.get().getHistoryLog() != null) {
            String existingHistory = latestLog.get().getHistoryLog();
            String newContent = taskLog.getContent() != null ? taskLog.getContent() : "";
            taskLog.setHistoryLog(existingHistory + "\n" + LocalDate.now() + ": " + newContent);
        } else {
            String content = taskLog.getContent() != null ? taskLog.getContent() : "";
            taskLog.setHistoryLog(LocalDate.now() + ": " + content);
        }
        
        return taskLogRepository.save(taskLog);
    }

    // 로그 수정
    @Transactional
    public TaskLog updateLog(Long logId, TaskLog updatedLog) {
        TaskLog taskLog = taskLogRepository.findById(logId)
                .orElseThrow(() -> new RuntimeException("TaskLog not found with id: " + logId));
        
        if (updatedLog.getContent() != null) {
            taskLog.setContent(updatedLog.getContent());
        }
        if (updatedLog.getNextAction() != null) {
            taskLog.setNextAction(updatedLog.getNextAction());
        }
        if (updatedLog.getDate() != null) {
            taskLog.setDate(updatedLog.getDate());
        }
        
        return taskLogRepository.save(taskLog);
    }

    // 로그 삭제
    @Transactional
    public void deleteLog(Long logId) {
        taskLogRepository.deleteById(logId);
    }

    // 특정 날짜의 로그 조회
    public List<TaskLog> getLogsByDate(LocalDate date) {
        return taskLogRepository.findByDate(date);
    }

    // 특정 작업의 특정 날짜 로그 조회
    public Optional<TaskLog> getLogByTaskAndDate(Long taskId, LocalDate date) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));
        return taskLogRepository.findByTaskAndDate(task, date);
    }
}

