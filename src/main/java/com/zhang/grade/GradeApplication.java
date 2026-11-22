package com.zhang.grade;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@MapperScan("com.zhang.grade.mapper")
@SpringBootApplication
public class GradeApplication {
    public static void main(String[] args) {
        SpringApplication.run(GradeApplication.class, args);
    }
}
