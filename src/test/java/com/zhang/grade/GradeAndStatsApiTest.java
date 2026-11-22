package com.zhang.grade;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("成绩与统计")
class GradeAndStatsApiTest extends BaseApiTest {

    @Test
    @DisplayName("成绩明细返回三表关联字段")
    void gradeDetail() throws Exception {
        mockMvc.perform(get("/api/grades").header("Authorization", bearer(teacherToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(288)))
                .andExpect(jsonPath("$.data[0].studentName").isNotEmpty())
                .andExpect(jsonPath("$.data[0].courseName").isNotEmpty());
    }

    @Test
    @DisplayName("按课程筛选成绩")
    void gradeFilterByCourse() throws Exception {
        mockMvc.perform(get("/api/grades?courseId=1").header("Authorization", bearer(teacherToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(org.hamcrest.Matchers.greaterThan(0)));
    }

    @Test
    @DisplayName("录入成绩成功")
    void createGrade() throws Exception {
        mockMvc.perform(post("/api/grades").header("Authorization", bearer(teacherToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"studentId\":1,\"courseId\":99,\"score\":88.5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @DisplayName("成绩超过 100 被 JSR-303 拦截")
    void scoreOverflow() throws Exception {
        mockMvc.perform(post("/api/grades").header("Authorization", bearer(teacherToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"studentId\":1,\"courseId\":98,\"score\":101}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.containsString("100")));
    }

    @Test
    @DisplayName("成绩为负被拦截")
    void scoreNegative() throws Exception {
        mockMvc.perform(post("/api/grades").header("Authorization", bearer(teacherToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"studentId\":1,\"courseId\":97,\"score\":-1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("学生角色录入成绩返回 403")
    void createGradeForbidden() throws Exception {
        mockMvc.perform(post("/api/grades").header("Authorization", bearer(studentToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"studentId\":1,\"courseId\":96,\"score\":60}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("统计返回课程口径与分数段")
    void stats() throws Exception {
        mockMvc.perform(get("/api/stats").header("Authorization", bearer(teacherToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.byCourse.length()").value(6))
                .andExpect(jsonPath("$.data.distribution.length()").value(org.hamcrest.Matchers.greaterThan(0)));
    }

    @Test
    @DisplayName("统计的均分在 0-100 之间")
    void statsRange() throws Exception {
        mockMvc.perform(get("/api/stats").header("Authorization", bearer(teacherToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.byCourse[0].avgScore")
                        .value(org.hamcrest.Matchers.allOf(
                                org.hamcrest.Matchers.greaterThanOrEqualTo(0.0),
                                org.hamcrest.Matchers.lessThanOrEqualTo(100.0))));
    }
}
