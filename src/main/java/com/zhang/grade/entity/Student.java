package com.zhang.grade.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("student")
public class Student {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String sno;
    private String name;
    private String gender;
    private String className;
    private String phone;
    @TableLogic
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
