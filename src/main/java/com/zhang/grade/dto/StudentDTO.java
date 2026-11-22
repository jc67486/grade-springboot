package com.zhang.grade.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class StudentDTO {
    @NotBlank(message = "学号不能为空")
    @Pattern(regexp = "^\\d{8}$", message = "学号必须为 8 位数字")
    private String sno;

    @NotBlank(message = "姓名不能为空")
    private String name;

    private String gender = "男";
    private String className;

    @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
}
