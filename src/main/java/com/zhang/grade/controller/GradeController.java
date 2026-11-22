package com.zhang.grade.controller;

import com.zhang.grade.common.AuthException;
import com.zhang.grade.common.Result;
import com.zhang.grade.dto.GradeDTO;
import com.zhang.grade.security.UserContext;
import com.zhang.grade.service.GradeService;
import com.zhang.grade.vo.GradeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;

    @GetMapping
    public Result<List<GradeVO>> list(@RequestParam(required = false) String keyword,
                                      @RequestParam(required = false) Long courseId) {
        return Result.ok(gradeService.detail(keyword, courseId));
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody GradeDTO dto) {
        if (!UserContext.get().isTeacher()) {
            throw new AuthException(403, "无权限：仅教师可录入成绩");
        }
        gradeService.create(dto);
        return Result.ok();
    }
}
