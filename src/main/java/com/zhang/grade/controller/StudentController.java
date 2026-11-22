package com.zhang.grade.controller;

import com.zhang.grade.common.AuthException;
import com.zhang.grade.common.PageResult;
import com.zhang.grade.common.Result;
import com.zhang.grade.dto.StudentDTO;
import com.zhang.grade.entity.Student;
import com.zhang.grade.security.UserContext;
import com.zhang.grade.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    public Result<PageResult<Student>> page(@RequestParam(defaultValue = "1") long page,
                                            @RequestParam(defaultValue = "10") long size,
                                            @RequestParam(required = false) String keyword,
                                            @RequestParam(name = "class", required = false) String className) {
        return Result.ok(studentService.page(page, size, keyword, className));
    }

    @GetMapping("/all")
    public Result<List<Student>> all() {
        return Result.ok(studentService.listAll());
    }

    @PostMapping
    public Result<Long> create(@Valid @RequestBody StudentDTO dto) {
        requireTeacher();
        return Result.ok(studentService.create(dto));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody StudentDTO dto) {
        requireTeacher();
        studentService.update(id, dto);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        requireTeacher();
        studentService.delete(id);
        return Result.ok();
    }

    private void requireTeacher() {
        if (!UserContext.get().isTeacher()) {
            throw new AuthException(403, "无权限：仅教师可维护学生");
        }
    }
}
