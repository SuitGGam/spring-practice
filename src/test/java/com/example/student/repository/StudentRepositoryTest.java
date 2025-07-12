package com.example.student.repository;

import com.example.student.domain.Student;
import com.example.student.dto.StudentDto;
import com.example.student.repository.StudentRepository;
import com.example.student.service.StudentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class StudentRepositoryTest {
    /*
    아직 이해도 많이 부족
    테스트 코드에 대한 이해도, Repo/Service에 대한 이해도 더 필요
     */
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private StudentService studentService;
    
    @Test
    @DisplayName("학생 정보를 조회한다.")
    public void findStudentTest() {
        //성공 케이스 - 학생 정보가 있는 경우
        //given: 학생 정보 저장
        Student student = studentRepository.save(Student.builder()
                .studentName("장동현")
                .age(29)
                .major("경영학과")
                .build());
        
        //when: findById를 호출하여 학생 정보를 조회
        Student found = studentRepository.findById(student.getStudentId()).orElse(null);
        
        //then: 조회 결과 모두 일치
        assertThat(found).isNotNull();
        assertThat(found.getStudentName()).isEqualTo("장동현");
        assertThat(found.getAge()).isEqualTo(29);
        assertThat(found.getMajor()).isEqualTo("경영학과");
        
        //실패 케이스 - 학생 정보가 없는 경우
        //given: 해당 학생 정보가 DB에 없도록 삭제
        studentRepository.deleteById(student.getStudentId());
        
        //when: findById를 호출
        Student notFound = studentRepository.findById(student.getStudentId()).orElse(null);
        
        //then: 조회 결과 null
        assertThat(notFound).isNull();
    }
    
    @Test
    @DisplayName("모든 학생 정보를 조회한다.")
    public void findAllStudentsTest() {
        //성공 케이스 - 학생 정보가 있는 경우
        //given: 테스트용 학생 정보 2개 저장
        studentRepository.save(Student.builder()
                .studentName("장동현")
                .age(29)
                .major("경영학과")
                .build());
        studentRepository.save(Student.builder()
                .studentName("최규환")
                .age(28)
                .major("기계공학과")
                .build());
        
        //when: findAll을 호출
        List<Student> students = studentRepository.findAll();
        
        //then: 조회 결과 모두 일치
        assertThat(students.size()).isEqualTo(2);
        assertThat(students).extracting("studentName")
                .containsExactly("장동현", "최규환");
        assertThat(students).extracting("major")
                .containsExactly("경영학과", "기계공학과");
        
        //실패 케이스 - 학생 정보가 없는 경우
        //given: 모든 학생 정보를 삭제한다.
        studentRepository.deleteAll();
        
        //when: findAll을 호출
        List<Student> emptyList = studentRepository.findAll();
        
        //then: 조회된 학생 0명
        assertThat(emptyList.size()).isEqualTo(0);
    }
    
    @Test
    @DisplayName("학생 정보를 추가한다.")
    public void addStudentTest() {
        //성공 케이스 : 중복된 학생 정보가 없는 경우
        //given: DB에 동일한 정보가 없는 테스트용 학생 정보 준비
        StudentDto dto = StudentDto.builder()
                .studentName("장동현")
                .age(29)
                .major("경영학과")
                .build();
        
        //when: addStudent로 학생 정보 등록
        Long savedId = studentService.addStudent(dto);
        
        //then: 등록한 정보 일치
        assertThat(savedId).isNotNull();
        StudentDto found = studentService.findStudent(savedId);
        assertThat(found.getStudentName()).isEqualTo(dto.getStudentName());
        assertThat(found.getAge()).isEqualTo(dto.getAge());
        assertThat(found.getMajor()).isEqualTo(dto.getMajor());
        
        //실패 케이스 - 중복된 학생 정보가 이미 있는 경우
        //given: 동일한 학생 정보를 한 번 더 등록한다.
        studentService.addStudent(dto);
        
        //when: 동일한 정보로 addStudent를 호출
        Throwable thrown = assertThrows(
                com.example.student.exception.DuplicateStudentException.class,
                () -> studentService.addStudent(dto)
        );
        
        //then: DuplicateStudentException 예외 발생
        assertThat(thrown).isInstanceOf(com.example.student.exception.DuplicateStudentException.class);
        assertThat(thrown.getMessage()).isEqualTo("이미 등록된 학생입니다.");
    }
    
    @Test
    @DisplayName("학생 정보를 수정한다.")
    public void updateStudentTest() {
        //성공 케이스 : 기존 학생 정보가 있는 경우
        //given: 테스트용 학생 정보 저장 및 수정할 학생 정보 준비
        Long savedId = studentService.addStudent(StudentDto.builder()
                .studentName("장동현")
                .age(29)
                .major("경영학과")
                .build());
        StudentDto updateDto = StudentDto.builder()
                .studentName("최규환")
                .age(28)
                .major("기계공학과")
                .build();
        
        //when: updateStudent로 학생 정보 수정
        studentService.updateStudent(savedId, updateDto);
        
        //then: 수정한 정보 일치
        StudentDto updated = studentService.findStudent(savedId);
        assertThat(updated.getStudentName()).isEqualTo("최규환");
        assertThat(updated.getAge()).isEqualTo(28);
        assertThat(updated.getMajor()).isEqualTo("기계공학과");
        
        //실패 케이스 : 수정하려는 학생 정보가 없는 경우
        //given: 해당 학생 정보가 DB에 없도록 삭제
        studentService.deleteStudent(savedId);
        
        //when: updateStudent로 삭제된 ID 수정
        Throwable thrown = assertThrows(jakarta.persistence.EntityNotFoundException.class, () -> {
            studentService.updateStudent(savedId, updateDto);
        });
        
        //then: EntityNotFoundException 예외 발생
        assertThat(thrown).isInstanceOf(jakarta.persistence.EntityNotFoundException.class);
    }
    
    @Test
    @DisplayName("학생 정보를 삭제한다.")
    public void deleteStudentTest() {
        //성공 케이스 : 기존 학생 정보가 있는 경우
        //given: 테스트용 학생 정보 저장
        StudentDto dto = StudentDto.builder()
                .studentName("장동현")
                .age(29)
                .major("경영학과")
                .build();
        Long savedId = studentService.addStudent(dto);
        
        //when: deleteStudent로 학생 정보 삭제
        studentService.deleteStudent(savedId);
        
        //then: EntityNotFoundException 예외 발생
        Throwable thrown = assertThrows(jakarta.persistence.EntityNotFoundException.class, () -> {
            studentService.findStudent(savedId);
        });
        
        //실패 케이스 : 삭제하려는 학생 정보가 없는 경우
        //given: 해당 학생 정보가 DB에 없도록 삭제
        studentService.deleteStudent(savedId);
        
        //when: deleteStudent로 삭제된 ID 삭제
        Throwable thrown2 = assertThrows(jakarta.persistence.EntityNotFoundException.class, () -> {
            studentService.deleteStudent(savedId);
        });
        
        //then: EntityNotFoundException 예외 발생
        assertThat(thrown2).isInstanceOf(jakarta.persistence.EntityNotFoundException.class);
    }
}