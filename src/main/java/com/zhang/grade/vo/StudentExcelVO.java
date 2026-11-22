package com.zhang.grade.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

/** Excel 导入导出模型：用注解绑定列名 */
@Data
@ColumnWidth(18)
public class StudentExcelVO {
    @ExcelProperty("学号")
    private String sno;

    @ExcelProperty("姓名")
    private String name;

    @ExcelProperty("性别")
    private String gender;

    @ExcelProperty("班级")
    private String className;

    @ExcelProperty("联系电话")
    private String phone;
}
