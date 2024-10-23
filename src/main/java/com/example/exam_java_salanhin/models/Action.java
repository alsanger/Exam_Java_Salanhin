package com.example.exam_java_salanhin.models;

import com.example.exam_java_salanhin.enums.ActionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "actions")
public class Action {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    private int quantity;

    private LocalDateTime actionDate;

    @ManyToOne
    @JoinColumn(name = "user_history_id")
    private UserHistory userHistory;
}