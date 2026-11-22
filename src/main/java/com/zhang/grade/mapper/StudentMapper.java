package com.zhang.grade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhang.grade.entity.Student;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {

    /**
     * 批量插入：一次 SQL 插多条，配合 JDBC 参数 rewriteBatchedStatements=true
     * INSERT IGNORE 保证学号重复时跳过而不是整批失败
     */
    @Insert("<script>"
            + "INSERT IGNORE INTO student(sno, name, gender, class_name, phone) VALUES "
            + "<foreach collection='list' item='it' separator=','>"
            + "(#{it.sno}, #{it.name}, #{it.gender}, #{it.className}, #{it.phone})"
            + "</foreach>"
            + "</script>")
    int insertBatch(@Param("list") List<Student> list);
}
