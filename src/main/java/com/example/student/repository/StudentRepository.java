package com.example.student.repository;

import com.example.student.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    /*
    이거 코드 적게 써도 되는 건지 모르겠다.
    왜 삶의 질이 올라간다고 한지 알 것 같다.
    MyBatis 쓰다가 JPA 쓰니 죄 짓는 기분
     */
    
    // 중복 등록 방지
    boolean existsByStudentNameAndAgeAndMajor(String studentName, int age, String major);
}