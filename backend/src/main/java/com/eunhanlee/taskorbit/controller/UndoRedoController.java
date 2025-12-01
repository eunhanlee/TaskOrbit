package com.eunhanlee.taskorbit.controller;

import com.eunhanlee.taskorbit.entity.GlobalLog;
import com.eunhanlee.taskorbit.service.GlobalLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/undo-redo")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UndoRedoController {

    private final GlobalLogService globalLogService;

    // Undo: 가장 최근 변경을 되돌림
    @PostMapping("/undo")
    public ResponseEntity<Map<String, Object>> undo() {
        Optional<GlobalLog> log = globalLogService.undo();
        
        Map<String, Object> response = new HashMap<>();
        if (log.isPresent()) {
            response.put("success", true);
            response.put("message", "Undo completed");
            response.put("log", log.get());
        } else {
            response.put("success", false);
            response.put("message", "No action to undo");
        }
        
        return ResponseEntity.ok(response);
    }

    // Redo: 되돌린 변경을 다시 적용
    @PostMapping("/redo")
    public ResponseEntity<Map<String, Object>> redo() {
        Optional<GlobalLog> log = globalLogService.redo();
        
        Map<String, Object> response = new HashMap<>();
        if (log.isPresent()) {
            response.put("success", true);
            response.put("message", "Redo completed");
            response.put("log", log.get());
        } else {
            response.put("success", false);
            response.put("message", "No action to redo");
        }
        
        return ResponseEntity.ok(response);
    }
}


