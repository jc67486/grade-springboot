package com.zhang.grade.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class GradeDTO {
    @NotNull(message = "学生必选")
    private Long studentId;

    @NotNull(message = "课程必选")
    private Long courseId;

    @NotNull(message = "成绩必填")
    @DecimalMin(value = "0", message = "成绩不能小于 0")
    @DecimalMax(value = "100", message = "成绩不能大于 100")
    private BigDecimal score;

    private String term = "2024-2025-1";
}
