package com.zhang.grade.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginVO {
    private String token;
    private Long id;
    private String username;
    private String name;
    private String role;
}
