package com.example.student.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDto {
    /*
    요청(등록/수정)과 응답(조회/삭제)를 분리해야 한다고 하는데
    아직 완전히 이해가 안 돼서 우선 하나로 작성
    특히 삭제 부분이 이해가 안 감
     */
    private Long studentId;     // 학번 (PK)
    private String studentName; // 이름
    private int age;            // 나이
    private String major;       // 전공
}
