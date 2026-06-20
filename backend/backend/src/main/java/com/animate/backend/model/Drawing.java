package com.animate.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "drawings")
@Getter
@Setter
public class Drawing {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Nome/título dado pelo usuário ao desenho
    private String title;

    // Dados do canvas em JSON (strokes, camadas, etc.)
    // Armazenado como jsonb no PostgreSQL — eficiente e consultável
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private String canvasData;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public Drawing() {}
}
