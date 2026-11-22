package com.zhang.grade.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zhang.grade.common.BizException;
import com.zhang.grade.dto.GradeDTO;
import com.zhang.grade.entity.Grade;
import com.zhang.grade.mapper.GradeMapper;
import com.zhang.grade.vo.GradeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GradeService {

    private final GradeMapper gradeMapper;

    public List<GradeVO> detail(String keyword, Long courseId) {
        return gradeMapper.selectDetail(keyword, courseId);
    }

    /** 录入成绩：同一学生同一课程不可重复 */
    @Transactional(rollbackFor = Exception.class)
    public void create(GradeDTO dto) {
        Long exists = gradeMapper.selectCount(Wrappers.<Grade>lambdaQuery()
                .eq(Grade::getStudentId, dto.getStudentId())
                .eq(Grade::getCourseId, dto.getCourseId()));
        if (exists != null && exists > 0) {
            throw new BizException("该学生该课程已有成绩");
        }
        Grade g = new Grade();
        g.setStudentId(dto.getStudentId());
        g.setCourseId(dto.getCourseId());
        g.setScore(dto.getScore());
        g.setTerm(dto.getTerm());
        gradeMapper.insert(g);
    }

    public List<Grade> listByStudent(Long studentId) {
        return gradeMapper.selectList(Wrappers.<Grade>lambdaQuery()
                .eq(Grade::getStudentId, studentId).orderByAsc(Grade::getCourseId));
    }
}
