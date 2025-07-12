package com.example.student.exception;

/*
StudentService에서 JPA가 제공하는 EntityNotFoundException을 사용
단순한 학생 CRUD여서 커스텀 예외가 따로 필요한 경우는 아님
하지만 공부 겸 우선 만들었음. 더 공부가 필요
 */
public class StudentNotFoundException extends RuntimeException {
    public StudentNotFoundException(Long studentId) {
        super("존재하지 않는 학생입니다. id=" + studentId);
    }
    
    public StudentNotFoundException(String message) {
        super(message);
    }
}