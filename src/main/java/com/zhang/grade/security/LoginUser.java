package com.zhang.grade.security;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginUser {
    private Long uid;
    private String username;
    private String role;

    public boolean isTeacher() {
        return "teacher".equals(role);
    }
}
