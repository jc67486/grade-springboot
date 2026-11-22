package com.zhang.grade.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhang.grade.common.BizException;
import com.zhang.grade.common.PageResult;
import com.zhang.grade.dto.StudentDTO;
import com.zhang.grade.entity.Student;
import com.zhang.grade.mapper.StudentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentMapper studentMapper;

    /** 分页 + 关键字 + 班级筛选 */
    public PageResult<Student> page(long page, long size, String keyword, String className) {
        LambdaQueryWrapper<Student> w = Wrappers.lambdaQuery();
        if (StringUtils.hasText(keyword)) {
            w.and(q -> q.like(Student::getName, keyword).or().like(Student::getSno, keyword));
        }
        if (StringUtils.hasText(className)) {
            w.eq(Student::getClassName, className);
        }
        w.orderByAsc(Student::getSno);
        return PageResult.of(studentMapper.selectPage(new Page<>(page, size), w));
    }

    @Transactional(rollbackFor = Exception.class)
    public Long create(StudentDTO dto) {
        Long exists = studentMapper.selectCount(
                Wrappers.<Student>lambdaQuery().eq(Student::getSno, dto.getSno()));
        if (exists != null && exists > 0) {
            throw new BizException("学号已存在：" + dto.getSno());
        }
        Student s = new Student();
        s.setSno(dto.getSno());
        s.setName(dto.getName());
        s.setGender(dto.getGender());
        s.setClassName(dto.getClassName());
        s.setPhone(dto.getPhone());
        studentMapper.insert(s);
        return s.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, StudentDTO dto) {
        Student s = studentMapper.selectById(id);
        if (s == null) {
            throw new BizException("学生不存在");
        }
        s.setName(dto.getName());
        s.setGender(dto.getGender());
        s.setClassName(dto.getClassName());
        s.setPhone(dto.getPhone());
        studentMapper.updateById(s);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        studentMapper.deleteById(id);
    }

    public List<Student> listAll() {
        return studentMapper.selectList(Wrappers.<Student>lambdaQuery().orderByAsc(Student::getSno));
    }
}
