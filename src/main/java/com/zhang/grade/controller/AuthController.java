package com.zhang.grade.controller;

import com.zhang.grade.common.Result;
import com.zhang.grade.dto.LoginDTO;
import com.zhang.grade.security.UserContext;
import com.zhang.grade.service.AuthService;
import com.zhang.grade.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.ok(authService.login(dto));
    }

    @GetMapping("/me")
    public Result<Object> me() {
        return Result.ok(UserContext.get());
    }
}
