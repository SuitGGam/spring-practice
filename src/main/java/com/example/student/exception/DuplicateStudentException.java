package com.example.student.exception;

public class DuplicateStudentException extends RuntimeException {
    // 중복 등록 방지
    public DuplicateStudentException(String message) {
        super(message);
    }
}