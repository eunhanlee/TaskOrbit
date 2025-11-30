package com.eunhanlee.taskorbit.service;

import com.eunhanlee.taskorbit.entity.GlobalLog;
import com.eunhanlee.taskorbit.entity.enums.ActionType;
import com.eunhanlee.taskorbit.repository.GlobalLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GlobalLogService {

    private final GlobalLogRepository globalLogRepository;

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
        // TODO: 실제 엔티티 복원 로직 구현 필요
        log.info("Undo: {} {} {}", latestLog.getActionType(), latestLog.getEntityType(), latestLog.getEntityId());
        
        return Optional.of(latestLog);
    }

    // Redo: 되돌린 변경을 다시 적용
    @Transactional
    public Optional<GlobalLog> redo() {
        // TODO: Redo 로직 구현 필요
        log.info("Redo: Not implemented yet");
        return Optional.empty();
    }

    // 특정 액션 타입의 로그 조회
    public List<GlobalLog> getLogsByActionType(ActionType actionType) {
        return globalLogRepository.findByActionTypeOrderByCreatedAtDesc(actionType);
    }
}


