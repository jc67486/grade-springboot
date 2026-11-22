package com.zhang.grade.service;

import com.zhang.grade.mapper.GradeMapper;
import com.zhang.grade.vo.CourseStatVO;
import com.zhang.grade.vo.ScoreSegVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final GradeMapper gradeMapper;

    /** 一次返回统计页所需的全部口径 */
    public Map<String, Object> overview() {
        List<CourseStatVO> byCourse = gradeMapper.selectCourseStat();
        List<ScoreSegVO> distribution = gradeMapper.selectScoreSegment();
        Map<String, Object> map = new HashMap<>();
        map.put("byCourse", byCourse);
        map.put("distribution", distribution);
        return map;
    }
}
