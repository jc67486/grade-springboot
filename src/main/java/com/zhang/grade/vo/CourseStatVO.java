package com.zhang.grade.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CourseStatVO {
    private String courseName;
    private BigDecimal avgScore;
    private BigDecimal maxScore;
    private BigDecimal minScore;
    private BigDecimal passRate;
}
