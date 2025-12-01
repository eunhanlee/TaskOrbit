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
        
        // History Log 업데이트: 모든 기존 로그를 날짜순으로 가져와서 누적
        List<TaskLog> allLogs = taskLogRepository.findByTaskIdOrderByDateDesc(taskId);
        StringBuilder historyBuilder = new StringBuilder();
        
        // 날짜순으로 정렬 (오래된 것부터) - 내림차순으로 가져온 것을 오름차순으로 정렬
        allLogs.sort((a, b) -> {
            int dateCompare = a.getDate().compareTo(b.getDate());
            if (dateCompare != 0) return dateCompare;
            // 같은 날짜면 생성 시간순
            return a.getCreatedAt().compareTo(b.getCreatedAt());
        });
        
        for (TaskLog log : allLogs) {
            if (log.getContent() != null && !log.getContent().trim().isEmpty()) {
                if (historyBuilder.length() > 0) {
                    historyBuilder.append("\n\n");
                }
                historyBuilder.append(log.getDate().toString())
                             .append(":\n")
                             .append(log.getContent());
            }
        }
        
        // 새 로그의 내용 추가
        if (taskLog.getContent() != null && !taskLog.getContent().trim().isEmpty()) {
            if (historyBuilder.length() > 0) {
                historyBuilder.append("\n\n");
            }
            historyBuilder.append(taskLog.getDate().toString())
                         .append(":\n")
                         .append(taskLog.getContent());
        }
        
        taskLog.setHistoryLog(historyBuilder.toString());
        
        return taskLogRepository.save(taskLog);
    }

    // 로그 수정
    @Transactional
    public TaskLog updateLog(Long logId, TaskLog updatedLog) {
        TaskLog taskLog = taskLogRepository.findById(logId)
                .orElseThrow(() -> new RuntimeException("TaskLog not found with id: " + logId));
        
        Long taskId = taskLog.getTask().getId();
        
        if (updatedLog.getContent() != null) {
            taskLog.setContent(updatedLog.getContent());
        }
        if (updatedLog.getNextAction() != null) {
            taskLog.setNextAction(updatedLog.getNextAction());
        }
        if (updatedLog.getDate() != null) {
            taskLog.setDate(updatedLog.getDate());
        }
        
        TaskLog saved = taskLogRepository.save(taskLog);
        
        // History Log 재계산: 모든 로그를 날짜순으로 가져와서 누적
        updateHistoryLogForTask(taskId);
        
        return saved;
    }
    
    // 특정 작업의 모든 로그의 History Log 업데이트
    @Transactional
    private void updateHistoryLogForTask(Long taskId) {
        List<TaskLog> allLogs = taskLogRepository.findByTaskIdOrderByDateDesc(taskId);
        
        // 날짜순으로 정렬 (오래된 것부터) - 내림차순으로 가져온 것을 오름차순으로 정렬
        allLogs.sort((a, b) -> {
            int dateCompare = a.getDate().compareTo(b.getDate());
            if (dateCompare != 0) return dateCompare;
            // 같은 날짜면 생성 시간순
            return a.getCreatedAt().compareTo(b.getCreatedAt());
        });
        
        StringBuilder historyBuilder = new StringBuilder();
        for (TaskLog log : allLogs) {
            if (log.getContent() != null && !log.getContent().trim().isEmpty()) {
                if (historyBuilder.length() > 0) {
                    historyBuilder.append("\n\n");
                }
                historyBuilder.append(log.getDate().toString())
                             .append(":\n")
                             .append(log.getContent());
            }
        }
        
        // 모든 로그의 historyLog 업데이트
        String accumulatedHistory = historyBuilder.toString();
        for (TaskLog log : allLogs) {
            log.setHistoryLog(accumulatedHistory);
            taskLogRepository.save(log);
        }
    }

    // 로그 삭제
    @Transactional
    public void deleteLog(Long logId) {
        TaskLog taskLog = taskLogRepository.findById(logId)
                .orElseThrow(() -> new RuntimeException("TaskLog not found with id: " + logId));
        Long taskId = taskLog.getTask().getId();
        
        taskLogRepository.deleteById(logId);
        
        // History Log 재계산
        updateHistoryLogForTask(taskId);
    }
    
    // 누적된 History Log 조회 (모든 로그의 Content를 날짜순으로 누적)
    public String getAccumulatedHistoryLog(Long taskId) {
        List<TaskLog> allLogs = taskLogRepository.findByTaskIdOrderByDateDesc(taskId);
        
        // 날짜순으로 정렬 (오래된 것부터) - 내림차순으로 가져온 것을 오름차순으로 정렬
        allLogs.sort((a, b) -> {
            int dateCompare = a.getDate().compareTo(b.getDate());
            if (dateCompare != 0) return dateCompare;
            // 같은 날짜면 생성 시간순
            return a.getCreatedAt().compareTo(b.getCreatedAt());
        });
        
        StringBuilder historyBuilder = new StringBuilder();
        for (TaskLog log : allLogs) {
            if (log.getContent() != null && !log.getContent().trim().isEmpty()) {
                if (historyBuilder.length() > 0) {
                    historyBuilder.append("\n\n");
                }
                historyBuilder.append(log.getDate().toString())
                             .append(":\n")
                             .append(log.getContent());
            }
        }
        
        return historyBuilder.toString();
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

