-- 初始化数据：48 名学生 / 6 门课程 / 288 条成绩
INSERT INTO sys_user (username, password, role, real_name) VALUES
('teacher', 'e10adc3949ba59abbe56e057f20f883e', 'teacher', '王建国'),
('student', 'e10adc3949ba59abbe56e057f20f883e', 'student', '张嘉诚');

INSERT INTO course (cno, name, credit, teacher) VALUES
('C001', '数据库原理与应用', 4.0, '陈老师'),
('C002', 'Python 程序设计', 3.5, '李老师'),
('C003', 'Java 程序设计', 4.0, '吴老师'),
('C004', 'Web 前端技术', 3.0, '王老师'),
('C005', '数据结构与算法', 3.5, '孙老师'),
('C006', '操作系统', 3.0, '周老师');

-- 用递归 CTE 批量生成 48 名学生
INSERT INTO student (sno, name, gender, class_name, phone)
WITH RECURSIVE seq(n) AS (
    SELECT 1 UNION ALL SELECT n + 1 FROM seq WHERE n < 48
)
SELECT CONCAT('2023', LPAD(n, 4, '0')),
       CONCAT(ELT(1 + (n % 8), '赵', '钱', '孙', '李', '周', '吴', '郑', '王'),
              ELT(1 + (n % 10), '子涵', '雨欣', '浩然', '思远', '嘉怡', '宇航', '梓萱', '俊杰', '欣怡', '博文')),
       IF(n % 2 = 0, '男', '女'),
       CONCAT('大数据230', 1 + (n % 4)),
       CONCAT('139', LPAD(n * 137 % 100000000, 8, '0'))
FROM seq;

-- 交叉连接生成 288 条成绩（40–100 分）
INSERT INTO grade (student_id, course_id, score, term)
SELECT s.id, c.id, ROUND(40 + RAND() * 60, 1), '2024-2025-1'
FROM student s CROSS JOIN course c;
