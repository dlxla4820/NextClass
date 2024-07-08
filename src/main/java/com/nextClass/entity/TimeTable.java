package com.nextClass.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Getter
@Builder
@Table(name = "time_table")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TimeTable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(columnDefinition = "binary(16)")
    private UUID uuid = UUID.randomUUID();

    @ManyToOne
    @JoinColumn(name = "member_uuid", referencedColumnName = "uuid", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "class_uuid", referencedColumnName = "uuid", nullable = false)
    private ClassDetail classDetail;

    private String week;

    @Column(name = "start_time")
    private int classStartTime;

    @Column(name = "end_time")
    private int classEndTime;

    private String semester;
}

