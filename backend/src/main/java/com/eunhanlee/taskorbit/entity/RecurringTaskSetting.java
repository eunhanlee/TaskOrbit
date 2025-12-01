package com.eunhanlee.taskorbit.entity;

import com.eunhanlee.taskorbit.entity.enums.RecurrenceType;
import com.eunhanlee.taskorbit.entity.enums.TaskSize;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "recurring_task_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecurringTaskSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(length = 100)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TaskSize size;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "recurrence_type", length = 50)
    private RecurrenceType recurrenceType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "recurrence_config", columnDefinition = "jsonb")
    private Map<String, Object> recurrenceConfig;

    @Column(nullable = false, name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(nullable = false, name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;
}



