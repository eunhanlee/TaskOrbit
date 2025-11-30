package com.eunhanlee.taskorbit.repository;

import com.eunhanlee.taskorbit.entity.Task;
import com.eunhanlee.taskorbit.entity.TaskCompletionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskCompletionRecordRepository extends JpaRepository<TaskCompletionRecord, Long> {

    // 특정 작업의 완료 기록
    Optional<TaskCompletionRecord> findByTask(Task task);

    // 특정 작업 ID의 완료 기록
    Optional<TaskCompletionRecord> findByTaskId(Long taskId);

    // 특정 날짜에 완료된 작업들
    List<TaskCompletionRecord> findByCompletedDate(LocalDate completedDate);

    // 특정 날짜 범위의 완료 기록
    List<TaskCompletionRecord> findByCompletedDateBetween(LocalDate startDate, LocalDate endDate);

    // 특정 날짜 이후의 완료 기록
    List<TaskCompletionRecord> findByCompletedDateAfter(LocalDate date);

    // 모든 완료 기록 (날짜순)
    List<TaskCompletionRecord> findAllByOrderByCompletedDateDesc();

    // 특정 작업의 완료 기록 존재 여부
    boolean existsByTaskId(Long taskId);
}

