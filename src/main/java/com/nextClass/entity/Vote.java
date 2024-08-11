package com.nextClass.entity;

import jakarta.persistence.*;
import lombok.*;
import org.checkerframework.checker.units.qual.C;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Entity
@Getter
@Table(name = "vote")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Builder.Default
    private UUID uuid = UUID.randomUUID();

    @ManyToOne
    @JoinColumn(name = "member_uuid")
    private Member member;

    @Column(name = "board_id")
    private String boardId;

    @Column(name = "board_type")
    private BoardType boardType;

    @Column(name = "reg_date")
    private LocalDateTime regDate;

    public enum BoardType {
        BOARD, COMMENT
    }
}
