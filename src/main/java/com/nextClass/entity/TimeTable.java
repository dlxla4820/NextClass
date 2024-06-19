package com.nextClass.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Builder
@Table(name = "time_table")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TimeTable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Builder.Default
    private UUID uuid = UUID.randomUUID();
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "member_uuid", referencedColumnName = "uuid", nullable = false)
//    private Member member;
    @ManyToOne
    @JoinColumn(name = "class_uuid", referencedColumnName = "uuid", nullable = false)
    private ClassDetail classDetail;
    private String week;
    //일단 int로 해놓고, 나중에 대한이가 된다고 하면 String으로 수정해서 enum타입 설정
    @Column(name = "time")
    private int classTime;
}
