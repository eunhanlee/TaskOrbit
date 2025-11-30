package com.eunhanlee.taskorbit.repository;

import com.eunhanlee.taskorbit.entity.GlobalLog;
import com.eunhanlee.taskorbit.entity.enums.ActionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GlobalLogRepository extends JpaRepository<GlobalLog, Long> {

    // 특정 엔티티의 모든 로그 (시간순)
    List<GlobalLog> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(String entityType, Long entityId);

    // 특정 엔티티 타입의 모든 로그
    List<GlobalLog> findByEntityTypeOrderByCreatedAtDesc(String entityType);

    // 특정 액션 타입의 로그
    List<GlobalLog> findByActionTypeOrderByCreatedAtDesc(ActionType actionType);

    // 모든 로그 (시간순 - Undo/Redo용)
    List<GlobalLog> findAllByOrderByCreatedAtDesc();

    // 특정 시간 이후의 로그
    List<GlobalLog> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime dateTime);

    // 특정 시간 범위의 로그
    @Query("SELECT gl FROM GlobalLog gl WHERE gl.createdAt BETWEEN :start AND :end ORDER BY gl.createdAt DESC")
    List<GlobalLog> findByCreatedAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // 특정 엔티티의 최신 로그
    @Query("SELECT gl FROM GlobalLog gl WHERE gl.entityType = :entityType AND gl.entityId = :entityId ORDER BY gl.createdAt DESC")
    Optional<GlobalLog> findTopByEntityTypeAndEntityIdOrderByCreatedAtDesc(@Param("entityType") String entityType, @Param("entityId") Long entityId);
}

