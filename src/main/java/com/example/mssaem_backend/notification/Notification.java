package com.example.mssaem_backend.notification;

import com.example.mssaem_backend.utils.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Notification extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @NotNull
    private String content;

    @ColumnDefault("false")
    private boolean state; // true : 읽음, false : 안 읽음

    @NotNull
    private Long resourceId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TypeEnum type;
}
