package com.example.student.controller;

import com.example.student.dto.StudentDto;
import com.example.student.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;
    
    // 학생 정보 등록
    @PostMapping
    public ResponseEntity<Long> addStudent(@RequestBody StudentDto studentDto) {
        Long studentId = studentService.addStudent(studentDto);
        return ResponseEntity.ok(studentId);
    }
    
    // 학생 정보 조회(1명)
    @GetMapping("/{studentId}")
    public ResponseEntity<StudentDto> findStudent(@PathVariable Long studentId) {
        StudentDto studentDto = studentService.findStudent(studentId);
        return ResponseEntity.ok(studentDto);
    }
    
    // 전체 학생 정보 조회
    @GetMapping
    public ResponseEntity<List<StudentDto>> findAllStudents() {
        List<StudentDto> students = studentService.findAllStudents();
        return ResponseEntity.ok(students);
    }
    
    // 학생 정보 수정
    @PutMapping("/{studentId}")
    public ResponseEntity<Void> updateStudent(@PathVariable Long studentId,
                                              @RequestBody StudentDto studentDto) {
        studentService.updateStudent(studentId, studentDto);
        return ResponseEntity.noContent().build();
    }
    
    // 학생 정보 삭제
    @DeleteMapping("/{studentId}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long studentId) {
        studentService.deleteStudent(studentId);
        return ResponseEntity.noContent().build();
    }
}