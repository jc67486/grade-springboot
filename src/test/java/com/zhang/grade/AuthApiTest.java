package com.zhang.grade;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("鉴权")
class AuthApiTest extends BaseApiTest {

    @Test
    @DisplayName("教师登录成功，返回 token 与角色")
    void teacherLogin() throws Exception {
        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"teacher\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.role").value("teacher"));
    }

    @Test
    @DisplayName("密码错误返回 401")
    void wrongPassword() throws Exception {
        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"teacher\",\"password\":\"bad\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    @DisplayName("用户名为空触发参数校验")
    void blankUsername() throws Exception {
        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"\",\"password\":\"123456\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    @DisplayName("无 token 访问受保护接口返回 401")
    void noToken() throws Exception {
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    @DisplayName("伪造 token 返回 401")
    void fakeToken() throws Exception {
        mockMvc.perform(get("/api/students").header("Authorization", "Bearer fake.jwt.token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.msg").value("token 无效"));
    }

    @Test
    @DisplayName("/api/auth/me 从 ThreadLocal 取当前用户")
    void currentUser() throws Exception {
        mockMvc.perform(get("/api/auth/me").header("Authorization", bearer(teacherToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("teacher"))
                .andExpect(jsonPath("$.data.role").value("teacher"));
    }

    @Test
    @DisplayName("健康检查免鉴权")
    void health() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("up"));
    }
}
