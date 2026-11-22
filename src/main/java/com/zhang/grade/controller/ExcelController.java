package com.zhang.grade.controller;

import com.zhang.grade.common.AuthException;
import com.zhang.grade.common.Result;
import com.zhang.grade.security.UserContext;
import com.zhang.grade.service.ExcelService;
import com.zhang.grade.vo.ImportResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/excel")
@RequiredArgsConstructor
public class ExcelController {

    private final ExcelService excelService;

    /** 导入学生（流式解析 + 批量入库） */
    @PostMapping("/import")
    public Result<ImportResultVO> importStudents(@RequestParam("file") MultipartFile file) throws IOException {
        if (!UserContext.get().isTeacher()) {
            throw new AuthException(403, "无权限：仅教师可导入数据");
        }
        if (file.isEmpty()) {
            throw new AuthException(400, "文件为空");
        }
        return Result.ok(excelService.importStudents(file.getBytes()));
    }

    /** 导出学生名单 */
    @GetMapping("/export")
    public void export(@RequestParam(defaultValue = "100000") int limit,
                       HttpServletResponse response) throws IOException {
        excelService.exportStudents(response, limit);
    }

    /** 下载导入模板 */
    @GetMapping("/template")
    public void template(HttpServletResponse response) throws IOException {
        excelService.exportTemplate(response);
    }
}
