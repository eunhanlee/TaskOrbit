package com.eunhanlee.taskorbit.service;

import com.eunhanlee.taskorbit.entity.RecurringTaskSetting;
import com.eunhanlee.taskorbit.entity.Task;
import com.eunhanlee.taskorbit.entity.enums.RecurrenceType;
import com.eunhanlee.taskorbit.repository.RecurringTaskSettingRepository;
import com.eunhanlee.taskorbit.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RecurringTaskSettingService {

    private final RecurringTaskSettingRepository recurringTaskSettingRepository;
    private final TaskRepository taskRepository;

    // 활성화된 반복 작업 설정 조회
    public List<RecurringTaskSetting> getActiveSettings() {
        return recurringTaskSettingRepository.findByIsActiveTrue();
    }

    // 모든 반복 작업 설정 조회
    public List<RecurringTaskSetting> getAllSettings() {
        return recurringTaskSettingRepository.findAll();
    }

    // 반복 작업 설정 생성
    @Transactional
    public RecurringTaskSetting createSetting(RecurringTaskSetting setting) {
        if (setting.getIsActive() == null) {
            setting.setIsActive(true);
        }
        return recurringTaskSettingRepository.save(setting);
    }

    // 반복 작업 설정 조회
    public RecurringTaskSetting getSetting(Long id) {
        return recurringTaskSettingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RecurringTaskSetting not found with id: " + id));
    }

    // 반복 작업 설정 수정
    @Transactional
    public RecurringTaskSetting updateSetting(Long id, RecurringTaskSetting updatedSetting) {
        RecurringTaskSetting setting = getSetting(id);
        
        if (updatedSetting.getTitle() != null) {
            setting.setTitle(updatedSetting.getTitle());
        }
        if (updatedSetting.getCategory() != null) {
            setting.setCategory(updatedSetting.getCategory());
        }
        if (updatedSetting.getSize() != null) {
            setting.setSize(updatedSetting.getSize());
        }
        if (updatedSetting.getRecurrenceType() != null) {
            setting.setRecurrenceType(updatedSetting.getRecurrenceType());
        }
        if (updatedSetting.getRecurrenceConfig() != null) {
            setting.setRecurrenceConfig(updatedSetting.getRecurrenceConfig());
        }
        if (updatedSetting.getIsActive() != null) {
            setting.setIsActive(updatedSetting.getIsActive());
        }
        
        return recurringTaskSettingRepository.save(setting);
    }

    // 반복 작업 설정 삭제
    @Transactional
    public void deleteSetting(Long id) {
        recurringTaskSettingRepository.deleteById(id);
    }

    // 반복 작업 설정 활성화/비활성화
    @Transactional
    public RecurringTaskSetting toggleActive(Long id) {
        RecurringTaskSetting setting = getSetting(id);
        setting.setIsActive(!setting.getIsActive());
        return recurringTaskSettingRepository.save(setting);
    }

    // 반복 작업 자동 생성 (매일 실행)
    @Transactional
    public void generateRecurringTasks() {
        List<RecurringTaskSetting> activeSettings = getActiveSettings();
        LocalDate today = LocalDate.now();
        int generatedCount = 0;
        
        for (RecurringTaskSetting setting : activeSettings) {
            if (shouldGenerateTask(setting, today)) {
                Task task = Task.builder()
                        .title(setting.getTitle())
                        .category(setting.getCategory())
                        .size(setting.getSize())
                        .dueDate(today)
                        .build();
                
                taskRepository.save(task);
                generatedCount++;
                log.info("Generated recurring task: {} (type: {})", task.getTitle(), setting.getRecurrenceType());
            }
        }
        
        log.info("Generated {} recurring tasks", generatedCount);
    }

    // 반복 작업 생성 여부 판단
    private boolean shouldGenerateTask(RecurringTaskSetting setting, LocalDate date) {
        // 이미 오늘 생성된 작업이 있는지 확인 (제목, 카테고리, dueDate로 중복 체크)
        List<Task> todayTasks = taskRepository.findByDueDate(date);
        boolean alreadyExists = todayTasks.stream()
                .anyMatch(task -> task.getTitle().equals(setting.getTitle()) &&
                        ((task.getCategory() == null && setting.getCategory() == null) ||
                         (task.getCategory() != null && task.getCategory().equals(setting.getCategory()))) &&
                        task.getDueDate().equals(date));
        
        if (alreadyExists) {
            return false;
        }
        
        // 반복 유형에 따라 생성 여부 결정
        switch (setting.getRecurrenceType()) {
            case DAILY:
                return true; // 매일 생성
            case WEEKLY:
                // 매주 같은 요일에 생성 (설정 생성일 기준)
                return date.getDayOfWeek() == setting.getCreatedAt().toLocalDate().getDayOfWeek();
            case MONTHLY:
                // 매월 같은 일자에 생성 (설정 생성일 기준)
                return date.getDayOfMonth() == setting.getCreatedAt().toLocalDate().getDayOfMonth();
            case YEARLY:
                // 매년 같은 월/일자에 생성 (설정 생성일 기준)
                return date.getMonthValue() == setting.getCreatedAt().toLocalDate().getMonthValue() &&
                       date.getDayOfMonth() == setting.getCreatedAt().toLocalDate().getDayOfMonth();
            default:
                return false;
        }
    }
}


