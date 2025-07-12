package com.example.student.controller;

import com.example.student.dto.StudentDto;
import com.example.student.repository.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class StudentControllerTest {
    /*
    @Transactional이 있어서 각 실패 케이스에 대해서 delete를 실행 안 해도 됨
    하지만 명시적으로 표기
     */
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Test
    @DisplayName("학생 정보를 조회한다.")
    public void findStudentTest() throws Exception {
        //성공 케이스 - 학생 정보가 있는 경우
        //given: 테스트용 학생 정보 저장
        StudentDto studentDto = StudentDto.builder()
                .studentName("장동현")
                .age(29)
                .major("경영학과")
                .build();
        Long savedId = studentRepository.save(
                com.example.student.domain.Student.builder()
                        .studentName(studentDto.getStudentName())
                        .age(studentDto.getAge())
                        .major(studentDto.getMajor())
                        .build()
        ).getStudentId();
        
        //when: GET /students/{student_id} 요청 수행
        mockMvc.perform(get("/students/" + savedId))
                // then: 응답 코드 200, 반환 데이터 검증
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId").value(savedId))
                .andExpect(jsonPath("$.studentName").value("장동현"))
                .andExpect(jsonPath("$.age").value(29))
                .andExpect(jsonPath("$.major").value("경영학과"));
        
        //실패 케이스 - 학생 정보가 없는 경우
        //given: 해당 학생 정보가 DB에 없도록 삭제
        studentRepository.deleteById(savedId);
        
        //when: GET /students/{student_id} 요청 수행
        mockMvc.perform(get("/students/" + savedId))
                // then: 404 Not Found 예외 또는 에러 메시지 검증
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("모든 학생 정보를 조회한다.")
    public void findAllStudentsTest() throws Exception {
        //성공 케이스 - 학생 정보가 있는 경우
        //given: 테스트용 학생 정보 2개 저장
        Long id1 = studentRepository.save(
                com.example.student.domain.Student.builder()
                        .studentName("장동현")
                        .age(29)
                        .major("경영학과")
                        .build()
        ).getStudentId();
        Long id2 = studentRepository.save(
                com.example.student.domain.Student.builder()
                        .studentName("최규환")
                        .age(28)
                        .major("기계공학과")
                        .build()
        ).getStudentId();
        
        //when: GET /students 요청 수행
        mockMvc.perform(get("/students"))
                // then: 응답 코드 200, 반환 데이터 검증
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()" ).value(2))
                .andExpect(jsonPath("$[0].studentName").value("장동현"))
                .andExpect(jsonPath("$[0].age").value(29))
                .andExpect(jsonPath("$[0].major").value("경영학과"))
                .andExpect(jsonPath("$[1].studentName").value("최규환"))
                .andExpect(jsonPath("$[1].age").value(28))
                .andExpect(jsonPath("$[1].major").value("기계공학과"));
        
        //실패 케이스 - 학생 정보가 없는 경우
        //given: 모든 학생 정보가 DB에 없도록 삭제
        studentRepository.deleteAll();
        
        //when: GET /students 요청 수행
        mockMvc.perform(get("/students"))
                // then: 빈 리스트 반환, 200 OK
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()" ).value(0));
    }
    
    @Test
    @DisplayName("학생 정보를 추가한다.")
    public void addStudentTest() throws Exception {
        //성공 케이스 : 중복된 학생 정보가 없는 경우
        //given: DB에 동일한 정보가 없는 테스트용 학생 정보 준비
        StudentDto studentDto = StudentDto.builder()
                .studentName("장동현")
                .age(29)
                .major("경영학과")
                .build();
        
        //when: POST /students 요청 수행
        mockMvc.perform(post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(studentDto)))
                // then: 응답 코드 200, 등록된 학생 정보 반환 및 DB에 저장 여부 검증
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.notNullValue()));
        
        //실패 케이스 : 중복된 학생 정보가 이미 있는 경우
        //given: DB에 동일한 학생 정보 미리 저장
        studentRepository.save(
                com.example.student.domain.Student.builder()
                        .studentName(studentDto.getStudentName())
                        .age(studentDto.getAge())
                        .major(studentDto.getMajor())
                        .build()
        );
        
        //when: POST /students 요청 수행
        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentDto)))
                // then: 응답 코드 400(Bad Request) 또는 409(Conflict), 에러 메시지 검증
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("학생 정보를 수정한다.")
    public void updateStudentTest() throws Exception {
        //성공 케이스 : 기존 학생 정보가 있는 경우
        //given: 테스트용 학생 정보 저장 및 수정할 학생 정보 준비
        Long savedId = studentRepository.save(
                com.example.student.domain.Student.builder()
                        .studentName("장동현")
                        .age(29)
                        .major("경영학과")
                        .build()
        ).getStudentId();
        
        StudentDto updateDto = StudentDto.builder()
                .studentName("최규환")
                .age(28)
                .major("기계공학과")
                .build();
        
        //when: PUT /students/{student_id} 요청 수행 (수정 데이터 포함)
        mockMvc.perform(put("/students/" + savedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                // then: 응답 코드 204(No Content)
                .andExpect(status().isNoContent());
        
        
        //실패 케이스 : 수정하려는 학생 정보가 없는 경우
        //given: 해당 학생 정보가 DB에 없도록 삭제
        studentRepository.deleteById(savedId);
        
        //when: PUT /students/{student_id} 요청 수행 (수정 데이터 포함)
        mockMvc.perform(put("/students/" + savedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                // then: 응답 코드 404(Not Found), 에러 메시지 검증
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("학생 정보를 삭제한다.")
    public void deleteStudentTest() throws Exception {
        //성공 케이스 : 기존 학생 정보가 있는 경우
        //given: 테스트용 학생 정보 저장
        Long savedId = studentRepository.save(
                com.example.student.domain.Student.builder()
                        .studentName("장동현")
                        .age(29)
                        .major("경영학과")
                        .build()
        ).getStudentId();
        
        //when: DELETE /students/{student_id} 요청 수행
        mockMvc.perform(delete("/students/" + savedId))
                // then: 응답 코드 204(No Content), 본문 없음
                .andExpect(status().isNoContent());
        
        //실패 케이스 : 삭제하려는 학생 정보가 없는 경우
        //given: 해당 학생 정보가 DB에 없도록 삭제
        studentRepository.deleteById(savedId);
        
        //when: DELETE /students/{student_id} 요청 수행
        mockMvc.perform(delete("/students/" + savedId))
                // then: 응답 코드 404(Not Found), 에러 메시지 검증
                .andExpect(status().isNotFound());
    }
}