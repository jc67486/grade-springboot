package com.zhang.grade.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GradeVO {
    private Long id;
    private String sno;
    private String studentName;
    private String className;
    private String courseName;
    private BigDecimal credit;
    private BigDecimal score;
    private String term;
}
