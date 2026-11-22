package com.zhang.grade;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("学生接口")
class StudentApiTest extends BaseApiTest {

    @Test
    @DisplayName("分页查询返回 list 与 total")
    void page() throws Exception {
        mockMvc.perform(get("/api/students?page=1&size=5").header("Authorization", bearer(teacherToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list.length()").value(5))
                .andExpect(jsonPath("$.data.total").value(org.hamcrest.Matchers.greaterThanOrEqualTo(48)));
    }

    @Test
    @DisplayName("按学号关键字搜索")
    void searchBySno() throws Exception {
        mockMvc.perform(get("/api/students?page=1&size=10&keyword=20230001")
                        .header("Authorization", bearer(teacherToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(org.hamcrest.Matchers.greaterThanOrEqualTo(1)));
    }

    @Test
    @DisplayName("按班级筛选")
    void filterByClass() throws Exception {
        mockMvc.perform(get("/api/students?class=大数据2302").header("Authorization", bearer(teacherToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list[0].className").value("大数据2302"));
    }

    @Test
    @DisplayName("教师新增学生成功")
    void createByTeacher() throws Exception {
        String body = "{\"sno\":\"20990001\",\"name\":\"测试同学\",\"gender\":\"男\","
                + "\"className\":\"大数据2301\",\"phone\":\"13900000000\"}";
        mockMvc.perform(post("/api/students").header("Authorization", bearer(teacherToken))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isNumber());
    }

    @Test
    @DisplayName("学号重复返回 400")
    void duplicateSno() throws Exception {
        String body = "{\"sno\":\"20990002\",\"name\":\"张三\",\"className\":\"大数据2301\"}";
        mockMvc.perform(post("/api/students").header("Authorization", bearer(teacherToken))
                .contentType(MediaType.APPLICATION_JSON).content(body));
        mockMvc.perform(post("/api/students").header("Authorization", bearer(teacherToken))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.containsString("学号已存在")));
    }

    @Test
    @DisplayName("学号不是 8 位数字触发 JSR-303 校验")
    void badSnoFormat() throws Exception {
        mockMvc.perform(post("/api/students").header("Authorization", bearer(teacherToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sno\":\"123\",\"name\":\"短学号\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.containsString("8 位数字")));
    }

    @Test
    @DisplayName("学生角色新增学生返回 403")
    void createForbiddenForStudent() throws Exception {
        mockMvc.perform(post("/api/students").header("Authorization", bearer(studentToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sno\":\"20990003\",\"name\":\"越权\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    @DisplayName("学生角色删除学生返回 403")
    void deleteForbiddenForStudent() throws Exception {
        mockMvc.perform(delete("/api/students/1").header("Authorization", bearer(studentToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("教师更新学生姓名")
    void update() throws Exception {
        String create = "{\"sno\":\"20990004\",\"name\":\"改名前\",\"className\":\"大数据2301\"}";
        String resp = mockMvc.perform(post("/api/students").header("Authorization", bearer(teacherToken))
                        .contentType(MediaType.APPLICATION_JSON).content(create))
                .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(resp).path("data").asLong();

        mockMvc.perform(put("/api/students/" + id).header("Authorization", bearer(teacherToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"改名后\",\"gender\":\"女\",\"className\":\"大数据2302\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("删除不存在的学生不报 500")
    void deleteNotExists() throws Exception {
        mockMvc.perform(delete("/api/students/999999").header("Authorization", bearer(teacherToken)))
                .andExpect(status().isOk());
    }
}
