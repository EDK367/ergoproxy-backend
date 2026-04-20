package com.ergoproxy.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "logs_info")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LogsInfo {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "UUID DEFAULT gen_random_uuid()", updatable = false, nullable = false)
    private UUID id;

    private String ip;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}