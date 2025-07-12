package com.example.student.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.AccessLevel;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

@Entity
@Table(name = "student")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // PK는 int보단 Long 권장
    // 왜 int가 아닌 Long으로 써야 하는지 좀 더 깊은 이해 필요
    private Long studentId;     // 학번, PK
    
    @Column(nullable = false)
    private String studentName; // 이름
    private int age;            // 나이
    private String major;       // 전공
    
    /*
    Setter 남용 방지, 캡슐화, 객체지향적 설계 등
    다양한 이유가 있지만 아직 이해를 못 했음
    더 깊이 있는 공부와 이해가 필요
     */
    // 학생 정보 수정
    public void update(String studentName, int age, String major) {
        this.studentName = studentName; // 이름
        this.age = age;                 // 나이
        this.major = major;             // 전공
    }
}