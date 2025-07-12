package com.example.student.service;

import com.example.student.domain.Student;
import com.example.student.dto.StudentDto;
import com.example.student.repository.StudentRepository;
import com.example.student.exception.DuplicateStudentException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import jakarta.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    
    // 학생 정보 등록
    // 학번을 왜 return 하는지 명확히 이해 X
    // 더 공부해야 함
    @Transactional
    public Long addStudent(StudentDto dto) {
        if (studentRepository.existsByStudentNameAndAgeAndMajor(
                dto.getStudentName(), dto.getAge(), dto.getMajor())) {
            throw new DuplicateStudentException("이미 등록된 학생입니다.");
        }
        Student student = Student.builder()
                .studentName(dto.getStudentName())
                .age(dto.getAge())
                .major(dto.getMajor())
                .build();
        Student saved = studentRepository.save(student);
        return saved.getStudentId();
    }
    
    // 학생 정보 조회(1명)
    @Transactional(readOnly = true)
    public StudentDto findStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 학생입니다. id=" + studentId));
        return StudentDto.builder()
                .studentId(student.getStudentId())
                .studentName(student.getStudentName())
                .age(student.getAge())
                .major(student.getMajor())
                .build();
    }
    
    // 전체 학생 정보 조회
    @Transactional(readOnly = true)
    public List<StudentDto> findAllStudents() {
        return studentRepository.findAll().stream()
                .map(student -> StudentDto.builder()
                        .studentId(student.getStudentId())
                        .studentName(student.getStudentName())
                        .age(student.getAge())
                        .major(student.getMajor())
                        .build())
                .collect(Collectors.toList());
    }
    
    // 학생 정보 수정
    @Transactional
    public void updateStudent(Long studentId, StudentDto dto) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 학생입니다. id=" + studentId));
        student.update(dto.getStudentName(), dto.getAge(), dto.getMajor());
    }
    
    // 학생 정보 삭제
    @Transactional
    public void deleteStudent(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new EntityNotFoundException("존재하지 않는 학생입니다. id=" + studentId);
        }
        studentRepository.deleteById(studentId);
    }
}