package com.eunhanlee.taskorbit.repository;

import com.eunhanlee.taskorbit.entity.Task;
import com.eunhanlee.taskorbit.entity.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Today 탭: schedule_date가 오늘 이하인 작업들
    @Query("SELECT t FROM Task t WHERE t.scheduleDate <= :date AND t.status != 'DONE' ORDER BY t.scheduleDate ASC, t.createdAt ASC")
    List<Task> findTodayTasks(@Param("date") LocalDate date);

    // Later 탭: schedule_date가 오늘 이후인 작업들
    @Query("SELECT t FROM Task t WHERE t.scheduleDate > :date ORDER BY t.scheduleDate ASC, t.createdAt ASC")
    List<Task> findLaterTasks(@Param("date") LocalDate date);

    // Done 탭: status가 DONE인 작업들
    List<Task> findByStatusOrderByUpdatedAtDesc(TaskStatus status);

    // Record 탭: 완료 기록이 있는 작업들 (TaskCompletionRecord와 조인)
    @Query("SELECT DISTINCT t FROM Task t INNER JOIN t.completionRecords cr ORDER BY cr.completedDate DESC")
    List<Task> findCompletedTasks();

    // 특정 날짜의 작업들
    List<Task> findByScheduleDate(LocalDate scheduleDate);

    // 특정 날짜 이하의 작업들
    List<Task> findByScheduleDateLessThanEqual(LocalDate date);

    // 특정 날짜 이후의 작업들
    List<Task> findByScheduleDateGreaterThan(LocalDate date);

    // 카테고리별 조회
    List<Task> findByCategory(String category);

    // 상태별 조회
    List<Task> findByStatus(TaskStatus status);

    // work_date로 조회
    List<Task> findByWorkDate(LocalDate workDate);
}

