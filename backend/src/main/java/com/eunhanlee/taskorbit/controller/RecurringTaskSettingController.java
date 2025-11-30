package com.eunhanlee.taskorbit.controller;

import com.eunhanlee.taskorbit.entity.RecurringTaskSetting;
import com.eunhanlee.taskorbit.service.RecurringTaskSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recurring-settings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RecurringTaskSettingController {

    private final RecurringTaskSettingService recurringTaskSettingService;

    // 활성화된 반복 작업 설정 조회
    @GetMapping("/active")
    public ResponseEntity<List<RecurringTaskSetting>> getActiveSettings() {
        return ResponseEntity.ok(recurringTaskSettingService.getActiveSettings());
    }

    // 모든 반복 작업 설정 조회
    @GetMapping
    public ResponseEntity<List<RecurringTaskSetting>> getAllSettings() {
        return ResponseEntity.ok(recurringTaskSettingService.getAllSettings());
    }

    // 반복 작업 설정 조회
    @GetMapping("/{id}")
    public ResponseEntity<RecurringTaskSetting> getSetting(@PathVariable Long id) {
        return ResponseEntity.ok(recurringTaskSettingService.getSetting(id));
    }

    // 반복 작업 설정 생성
    @PostMapping
    public ResponseEntity<RecurringTaskSetting> createSetting(
            @RequestBody RecurringTaskSetting setting) {
        RecurringTaskSetting created = recurringTaskSettingService.createSetting(setting);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 반복 작업 설정 수정
    @PutMapping("/{id}")
    public ResponseEntity<RecurringTaskSetting> updateSetting(
            @PathVariable Long id,
            @RequestBody RecurringTaskSetting setting) {
        RecurringTaskSetting updated = recurringTaskSettingService.updateSetting(id, setting);
        return ResponseEntity.ok(updated);
    }

    // 반복 작업 설정 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSetting(@PathVariable Long id) {
        recurringTaskSettingService.deleteSetting(id);
        return ResponseEntity.noContent().build();
    }

    // 반복 작업 설정 활성화/비활성화
    @PostMapping("/{id}/toggle")
    public ResponseEntity<RecurringTaskSetting> toggleActive(@PathVariable Long id) {
        return ResponseEntity.ok(recurringTaskSettingService.toggleActive(id));
    }
}


