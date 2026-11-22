package com.zhang.grade.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("grade")
public class Grade {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private Long courseId;
    private BigDecimal score;
    private String term;
    @TableLogic
    private Integer deleted;
}
