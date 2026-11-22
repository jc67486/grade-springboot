package com.zhang.grade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhang.grade.entity.Grade;
import com.zhang.grade.vo.CourseStatVO;
import com.zhang.grade.vo.GradeVO;
import com.zhang.grade.vo.ScoreSegVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GradeMapper extends BaseMapper<Grade> {

    /** 成绩明细：三表关联，支持按姓名/学号与课程筛选 */
    @Select("<script>"
            + "SELECT g.id, s.sno, s.name AS studentName, s.class_name AS className, "
            + "c.name AS courseName, c.credit, g.score, g.term "
            + "FROM grade g JOIN student s ON s.id = g.student_id "
            + "JOIN course c ON c.id = g.course_id "
            + "WHERE g.deleted = 0 "
            + "<if test='keyword != null and keyword != \"\"'>"
            + " AND (s.name LIKE CONCAT('%', #{keyword}, '%') OR s.sno LIKE CONCAT('%', #{keyword}, '%'))"
            + "</if>"
            + "<if test='courseId != null'> AND g.course_id = #{courseId}</if>"
            + " ORDER BY s.sno, c.cno"
            + "</script>")
    List<GradeVO> selectDetail(@Param("keyword") String keyword, @Param("courseId") Long courseId);

    /** 各课程统计：均分 / 最高 / 最低 / 及格率 */
    @Select("SELECT c.name AS courseName, ROUND(AVG(g.score),1) AS avgScore, "
            + "MAX(g.score) AS maxScore, MIN(g.score) AS minScore, "
            + "ROUND(SUM(CASE WHEN g.score >= 60 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 1) AS passRate "
            + "FROM grade g JOIN course c ON c.id = g.course_id "
            + "WHERE g.deleted = 0 GROUP BY g.course_id ORDER BY c.cno")
    List<CourseStatVO> selectCourseStat();

    /** 分数段分布 */
    @Select("SELECT CASE WHEN score >= 90 THEN '优秀(90+)' "
            + "WHEN score >= 80 THEN '良好(80-89)' "
            + "WHEN score >= 70 THEN '中等(70-79)' "
            + "WHEN score >= 60 THEN '及格(60-69)' ELSE '不及格(<60)' END AS segment, "
            + "COUNT(*) AS cnt FROM grade WHERE deleted = 0 GROUP BY segment")
    List<ScoreSegVO> selectScoreSegment();
}
