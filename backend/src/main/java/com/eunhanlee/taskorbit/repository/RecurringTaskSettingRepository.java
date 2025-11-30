package com.eunhanlee.taskorbit.repository;

import com.eunhanlee.taskorbit.entity.RecurringTaskSetting;
import com.eunhanlee.taskorbit.entity.enums.RecurrenceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecurringTaskSettingRepository extends JpaRepository<RecurringTaskSetting, Long> {

    // 활성화된 반복 작업 설정만 조회
    List<RecurringTaskSetting> findByIsActiveTrue();

    // 비활성화된 반복 작업 설정 조회
    List<RecurringTaskSetting> findByIsActiveFalse();

    // 특정 반복 유형의 설정 조회
    List<RecurringTaskSetting> findByRecurrenceType(RecurrenceType recurrenceType);

    // 활성화된 특정 반복 유형의 설정 조회
    List<RecurringTaskSetting> findByRecurrenceTypeAndIsActiveTrue(RecurrenceType recurrenceType);

    // 카테고리별 조회
    List<RecurringTaskSetting> findByCategory(String category);
}


