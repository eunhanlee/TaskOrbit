package com.eunhanlee.taskorbit.repository;

import com.eunhanlee.taskorbit.entity.Task;
import com.eunhanlee.taskorbit.entity.TaskLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskLogRepository extends JpaRepository<TaskLog, Long> {

    // 특정 작업의 모든 로그
    List<TaskLog> findByTaskOrderByDateDesc(Task task);

    // 특정 작업의 최신 로그 (Next Action 표시용) - 첫 번째 결과만
    @Query("SELECT tl FROM TaskLog tl WHERE tl.task.id = :taskId ORDER BY tl.date DESC, tl.createdAt DESC")
    List<TaskLog> findLatestLogsByTaskId(@Param("taskId") Long taskId);

    // 특정 작업의 특정 날짜 로그
    Optional<TaskLog> findByTaskAndDate(Task task, LocalDate date);

    // 특정 작업의 모든 로그 (날짜순)
    List<TaskLog> findByTaskIdOrderByDateDesc(Long taskId);

    // 특정 날짜의 모든 로그
    List<TaskLog> findByDate(LocalDate date);

    // 특정 작업의 로그 개수
    long countByTask(Task task);
}

