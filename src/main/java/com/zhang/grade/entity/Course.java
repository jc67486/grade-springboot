package com.zhang.grade.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("course")
public class Course {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String cno;
    private String name;
    private BigDecimal credit;
    private String teacher;
    @TableLogic
    private Integer deleted;
}
