package com.eunhanlee.taskorbit.util;

import com.eunhanlee.taskorbit.entity.Task;

import java.util.HashMap;
import java.util.Map;

public class TaskConverter {
    
    public static Map<String, Object> taskToMap(Task task) {
        if (task == null) {
            return null;
        }
        
        Map<String, Object> map = new HashMap<>();
        map.put("id", task.getId());
        map.put("title", task.getTitle());
        map.put("category", task.getCategory());
        map.put("size", task.getSize() != null ? task.getSize().name() : null);
        map.put("status", task.getStatus() != null ? task.getStatus().name() : null);
        map.put("dueDate", task.getDueDate() != null ? task.getDueDate().toString() : null);
        map.put("createdAt", task.getCreatedAt() != null ? task.getCreatedAt().toString() : null);
        return map;
    }
}

