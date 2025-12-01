package com.eunhanlee.taskorbit.service;

import com.eunhanlee.taskorbit.entity.GlobalLog;
import com.eunhanlee.taskorbit.entity.Task;
import com.eunhanlee.taskorbit.entity.TaskCompletionRecord;
import com.eunhanlee.taskorbit.entity.enums.ActionType;
import com.eunhanlee.taskorbit.entity.enums.TaskSize;
import com.eunhanlee.taskorbit.entity.enums.TaskStatus;
import com.eunhanlee.taskorbit.repository.GlobalLogRepository;
import com.eunhanlee.taskorbit.repository.TaskCompletionRecordRepository;
import com.eunhanlee.taskorbit.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GlobalLogService {

    private final GlobalLogRepository globalLogRepository;
    private final TaskRepository taskRepository;
    private final TaskCompletionRecordRepository completionRecordRepository;

    // 로그 생성
    @Transactional
    public GlobalLog createLog(String entityType, Long entityId, ActionType actionType, 
                               Map<String, Object> oldData, Map<String, Object> newData) {
        GlobalLog log = GlobalLog.builder()
                .entityType(entityType)
                .entityId(entityId)
                .actionType(actionType)
                .oldData(oldData)
                .newData(newData)
                .build();
        
        return globalLogRepository.save(log);
    }

    // 특정 엔티티의 모든 로그 조회
    public List<GlobalLog> getEntityLogs(String entityType, Long entityId) {
        return globalLogRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType, entityId);
    }

    // 모든 로그 조회 (Undo/Redo용)
    public List<GlobalLog> getAllLogs() {
        return globalLogRepository.findAllByOrderByCreatedAtDesc();
    }

    // 최신 로그 조회
    public Optional<GlobalLog> getLatestLog(String entityType, Long entityId) {
        return globalLogRepository.findTopByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType, entityId);
    }

    // Undo: 가장 최근 변경을 되돌림
    @Transactional
    public Optional<GlobalLog> undo() {
        List<GlobalLog> logs = getAllLogs();
        if (logs.isEmpty()) {
            return Optional.empty();
        }
        
        GlobalLog latestLog = logs.get(0);
        
        // Task 엔티티만 지원 (현재)
        if (!"Task".equals(latestLog.getEntityType())) {
            log.warn("Undo not supported for entity type: {}", latestLog.getEntityType());
            return Optional.empty();
        }
        
        try {
            // GlobalLog 생성 방지를 위한 플래그 설정 (restoreTaskFromMap에서 사용)
            switch (latestLog.getActionType()) {
                case CREATE:
                    // 생성된 작업 삭제 (TaskCompletionRecord는 자동으로 삭제됨 - CASCADE)
                    taskRepository.deleteById(latestLog.getEntityId());
                    log.info("Undo CREATE: Deleted task {}", latestLog.getEntityId());
                    break;
                    
                case UPDATE:
                    // oldData로 복원 (GlobalLog 생성 없이 직접 복원)
                    restoreTaskFromMap(latestLog.getEntityId(), latestLog.getOldData(), false);
                    log.info("Undo UPDATE: Restored task {} from old data", latestLog.getEntityId());
                    break;
                    
                case DELETE:
                    // newData로 복원 (삭제 전 데이터)
                    restoreTaskFromMap(latestLog.getEntityId(), latestLog.getNewData(), false);
                    log.info("Undo DELETE: Restored task {} from new data", latestLog.getEntityId());
                    break;
            }
            
            // Undo된 로그는 삭제하지 않고 유지 (Redo를 위해)
            return Optional.of(latestLog);
        } catch (Exception e) {
            log.error("Error during undo operation", e);
            return Optional.empty();
        }
    }

    // Redo: 되돌린 변경을 다시 적용
    @Transactional
    public Optional<GlobalLog> redo() {
        // 최근 undo된 로그를 다시 적용
        List<GlobalLog> logs = getAllLogs();
        if (logs.isEmpty()) {
            return Optional.empty();
        }
        
        GlobalLog latestLog = logs.get(0);
        
        if (!"Task".equals(latestLog.getEntityType())) {
            log.warn("Redo not supported for entity type: {}", latestLog.getEntityType());
            return Optional.empty();
        }
        
        try {
            switch (latestLog.getActionType()) {
                case CREATE:
                    // newData로 다시 생성
                    restoreTaskFromMap(latestLog.getEntityId(), latestLog.getNewData(), false);
                    log.info("Redo CREATE: Recreated task {}", latestLog.getEntityId());
                    break;
                    
                case UPDATE:
                    // newData로 다시 적용
                    restoreTaskFromMap(latestLog.getEntityId(), latestLog.getNewData(), false);
                    log.info("Redo UPDATE: Reapplied task {} with new data", latestLog.getEntityId());
                    break;
                    
                case DELETE:
                    // 다시 삭제
                    taskRepository.deleteById(latestLog.getEntityId());
                    log.info("Redo DELETE: Deleted task {}", latestLog.getEntityId());
                    break;
            }
            
            return Optional.of(latestLog);
        } catch (Exception e) {
            log.error("Error during redo operation", e);
            return Optional.empty();
        }
    }
    
    // Map에서 Task 복원 (skipLog: GlobalLog 생성 여부)
    private void restoreTaskFromMap(Long taskId, Map<String, Object> data, boolean skipLog) {
        if (data == null) {
            return;
        }
        
        Task task = taskRepository.findById(taskId).orElse(null);
        TaskStatus oldStatus = task != null ? task.getStatus() : null;
        
        if (task == null) {
            // 삭제된 경우 새로 생성
            task = Task.builder()
                    .id(taskId)
                    .title((String) data.get("title"))
                    .category((String) data.get("category"))
                    .size(data.get("size") != null ? TaskSize.valueOf((String) data.get("size")) : null)
                    .status(data.get("status") != null ? TaskStatus.valueOf((String) data.get("status")) : TaskStatus.ONGOING)
                    .dueDate(data.get("dueDate") != null ? LocalDate.parse((String) data.get("dueDate")) : LocalDate.now())
                    .build();
        } else {
            // 기존 작업 업데이트
            if (data.get("title") != null) {
                task.setTitle((String) data.get("title"));
            }
            if (data.get("category") != null) {
                task.setCategory((String) data.get("category"));
            }
            if (data.get("size") != null) {
                task.setSize(TaskSize.valueOf((String) data.get("size")));
            }
            if (data.get("status") != null) {
                task.setStatus(TaskStatus.valueOf((String) data.get("status")));
            }
            if (data.get("dueDate") != null) {
                task.setDueDate(LocalDate.parse((String) data.get("dueDate")));
            }
        }
        
        Task savedTask = taskRepository.save(task);
        
        // TaskCompletionRecord 처리
        TaskStatus newStatus = savedTask.getStatus();
        if (oldStatus == TaskStatus.DONE && newStatus != TaskStatus.DONE) {
            // DONE에서 다른 상태로 변경: 완료 기록 삭제
            completionRecordRepository.findByTaskId(taskId).ifPresent(completionRecordRepository::delete);
        } else if (oldStatus != TaskStatus.DONE && newStatus == TaskStatus.DONE) {
            // 다른 상태에서 DONE으로 변경: 완료 기록 생성
            if (!completionRecordRepository.existsByTaskId(taskId)) {
                TaskCompletionRecord record = TaskCompletionRecord.builder()
                        .task(savedTask)
                        .completedDate(LocalDate.now())
                        .build();
                completionRecordRepository.save(record);
            }
        }
    }

    // 특정 액션 타입의 로그 조회
    public List<GlobalLog> getLogsByActionType(ActionType actionType) {
        return globalLogRepository.findByActionTypeOrderByCreatedAtDesc(actionType);
    }
}


