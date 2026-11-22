package com.zhang.grade;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/** 测试基类：每个用例事务回滚，不污染开发库 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public abstract class BaseApiTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected String teacherToken;
    protected String studentToken;

    @BeforeEach
    void loginBoth() throws Exception {
        teacherToken = login("teacher");
        studentToken = login("student");
    }

    protected String login(String username) throws Exception {
        String body = "{\"username\":\"" + username + "\",\"password\":\"123456\"}";
        String json = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andReturn().getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(json);
        return node.path("data").path("token").asText();
    }

    protected String bearer(String token) {
        return "Bearer " + token;
    }

    protected String json(Object o) throws Exception {
        return objectMapper.writeValueAsString(o);
    }
}
