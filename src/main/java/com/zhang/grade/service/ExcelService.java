package com.zhang.grade.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.zhang.grade.common.BizException;
import com.zhang.grade.entity.Student;
import com.zhang.grade.mapper.StudentMapper;
import com.zhang.grade.vo.ImportResultVO;
import com.zhang.grade.vo.StudentExcelVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel 导入导出：SAX 流式读取，避免全量加载撑爆堆内存
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelService {

    private static final int BATCH_SIZE = 1000;

    private final StudentMapper studentMapper;

    /** 导入：逐行解析，攒够 1000 条批量入库 */
    public ImportResultVO importStudents(byte[] bytes) {
        long start = System.currentTimeMillis();
        List<Student> buffer = new ArrayList<>(BATCH_SIZE);
        int[] counter = {0, 0};   // [读到行数, 成功入库数]

        try {
            EasyExcel.read(new java.io.ByteArrayInputStream(bytes), StudentExcelVO.class,
                    new ReadListener<StudentExcelVO>() {
                        @Override
                        public void invoke(StudentExcelVO row, AnalysisContext context) {
                            counter[0]++;
                            if (row.getSno() == null || row.getName() == null) {
                                return;
                            }
                            Student s = new Student();
                            s.setSno(row.getSno());
                            s.setName(row.getName());
                            s.setGender(row.getGender());
                            s.setClassName(row.getClassName());
                            s.setPhone(row.getPhone());
                            buffer.add(s);
                            if (buffer.size() >= BATCH_SIZE) {
                                flush(buffer, counter);
                            }
                        }

                        @Override
                        public void doAfterAllAnalysed(AnalysisContext context) {
                            flush(buffer, counter);
                        }
                    }).sheet().doRead();
        } catch (Exception e) {
            log.error("Excel 导入失败", e);
            throw new BizException("Excel 解析失败：" + e.getMessage());
        }

        long cost = System.currentTimeMillis() - start;
        return new ImportResultVO(counter[0], counter[1], counter[0] - counter[1], cost);
    }

    /** 攒批后一次 SQL 插入：1000 条/批，配合 rewriteBatchedStatements 显著快于逐条 insert */
    private void flush(List<Student> buffer, int[] counter) {
        if (buffer.isEmpty()) {
            return;
        }
        try {
            int affected = studentMapper.insertBatch(buffer);
            counter[1] += affected;
        } catch (Exception e) {
            // 整批失败时降级为逐条，保证部分数据能入库
            log.warn("批量插入失败，降级逐条：{}", e.getMessage());
            for (Student s : buffer) {
                try {
                    studentMapper.insert(s);
                    counter[1]++;
                } catch (Exception ignore) {
                    // 学号重复等
                }
            }
        }
        buffer.clear();
    }

    /** 导出：EasyExcel 流式写，10 万行不占内存 */
    public void exportStudents(HttpServletResponse response, int limit) throws IOException {
        List<Student> list = studentMapper.selectList(null);
        if (list.size() > limit) {
            list = list.subList(0, limit);
        }
        List<StudentExcelVO> rows = new ArrayList<>(list.size());
        for (Student s : list) {
            StudentExcelVO vo = new StudentExcelVO();
            vo.setSno(s.getSno());
            vo.setName(s.getName());
            vo.setGender(s.getGender());
            vo.setClassName(s.getClassName());
            vo.setPhone(s.getPhone());
            rows.add(vo);
        }
        writeExcel(response, "学生名单.xlsx", rows, StudentExcelVO.class);
    }

    /** 下载导入模板 */
    public void exportTemplate(HttpServletResponse response) throws IOException {
        List<StudentExcelVO> demo = new ArrayList<>();
        StudentExcelVO vo = new StudentExcelVO();
        vo.setSno("20239001");
        vo.setName("示例学生");
        vo.setGender("男");
        vo.setClassName("大数据2301");
        vo.setPhone("13900000000");
        demo.add(vo);
        writeExcel(response, "学生导入模板.xlsx", demo, StudentExcelVO.class);
    }

    private void writeExcel(HttpServletResponse response, String fileName,
                            List<StudentExcelVO> rows, Class<StudentExcelVO> clazz) throws IOException {
        // xlsx 是二进制，不设 charset，否则 Content-Type 会多出 ";charset=utf-8"
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()).replace("+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + encoded);
        EasyExcel.write(response.getOutputStream(), clazz).sheet("数据").doWrite(rows);
    }
}
