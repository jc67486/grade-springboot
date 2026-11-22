package com.zhang.grade;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Excel 导入导出")
class ExcelApiTest extends BaseApiTest {

    /** 用 StudentExcelVO 写出文件，表头与读取模型完全一致，不会产生多级表头 */
    private MockMultipartFile buildXlsx(boolean withRows) {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        java.util.List<com.zhang.grade.vo.StudentExcelVO> rows = new java.util.ArrayList<>();
        if (withRows) {
            rows.add(row("20991001", "导入甲", "男", "大数据2401", "13800000001"));
            rows.add(row("20991002", "导入乙", "女", "大数据2402", "13800000002"));
        }
        com.alibaba.excel.EasyExcel.write(out, com.zhang.grade.vo.StudentExcelVO.class)
                .sheet("data").doWrite(rows);
        return new MockMultipartFile("file", "students.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", out.toByteArray());
    }

    private com.zhang.grade.vo.StudentExcelVO row(String sno, String name, String gender,
                                                  String cls, String phone) {
        com.zhang.grade.vo.StudentExcelVO vo = new com.zhang.grade.vo.StudentExcelVO();
        vo.setSno(sno);
        vo.setName(name);
        vo.setGender(gender);
        vo.setClassName(cls);
        vo.setPhone(phone);
        return vo;
    }

    @Test
    @DisplayName("教师导入 Excel，返回读取/成功/失败与耗时")
    void importOk() throws Exception {
        mockMvc.perform(multipart("/api/excel/import").file(buildXlsx(true))
                        .header("Authorization", bearer(teacherToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(2))
                .andExpect(jsonPath("$.data.success").value(2))
                .andExpect(jsonPath("$.data.failed").value(0))
                .andExpect(jsonPath("$.data.costMs").isNumber());
    }

    @Test
    @DisplayName("学生角色导入返回 403")
    void importForbidden() throws Exception {
        mockMvc.perform(multipart("/api/excel/import").file(buildXlsx(true))
                        .header("Authorization", bearer(studentToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("只有表头没有数据行时返回 0 行")
    void importHeaderOnly() throws Exception {
        mockMvc.perform(multipart("/api/excel/import").file(buildXlsx(false))
                        .header("Authorization", bearer(teacherToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(0))
                .andExpect(jsonPath("$.data.success").value(0));
    }

    @Test
    @DisplayName("字节为空的文件返回 4xx")
    void importEmptyBytes() throws Exception {
        MockMultipartFile empty = new MockMultipartFile("file", "empty.xlsx",
                "application/octet-stream", new byte[0]);
        mockMvc.perform(multipart("/api/excel/import").file(empty)
                        .header("Authorization", bearer(teacherToken)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("导出返回 xlsx 与附件头")
    void export() throws Exception {
        mockMvc.perform(get("/api/excel/export?limit=10").header("Authorization", bearer(teacherToken)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString("attachment")))
                .andExpect(content().contentType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
    }

    @Test
    @DisplayName("模板下载可用")
    void template() throws Exception {
        mockMvc.perform(get("/api/excel/template").header("Authorization", bearer(teacherToken)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString("attachment")));
    }
}
