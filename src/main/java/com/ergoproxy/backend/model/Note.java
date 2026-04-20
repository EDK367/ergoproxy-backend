package com.ergoproxy.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notes")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Note {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "UUID DEFAULT gen_random_uuid()", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder.Default
    private boolean enabled = true;

    @Builder.Default
    private boolean important = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}