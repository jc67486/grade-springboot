-- 学生成绩管理系统 · 表结构
DROP TABLE IF EXISTS grade;
DROP TABLE IF EXISTS course;
DROP TABLE IF EXISTS student;
DROP TABLE IF EXISTS sys_user;

CREATE TABLE student (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    sno         VARCHAR(20)  NOT NULL UNIQUE COMMENT '学号',
    name        VARCHAR(32)  NOT NULL COMMENT '姓名',
    gender      VARCHAR(4)   DEFAULT '男',
    class_name  VARCHAR(32),
    phone       VARCHAR(20),
    deleted     TINYINT      DEFAULT 0,
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    KEY idx_class (class_name)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '学生';

CREATE TABLE course (
    id      BIGINT PRIMARY KEY AUTO_INCREMENT,
    cno     VARCHAR(20) NOT NULL UNIQUE COMMENT '课程编号',
    name    VARCHAR(64) NOT NULL,
    credit  DECIMAL(3, 1),
    teacher VARCHAR(32),
    deleted TINYINT DEFAULT 0
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '课程';

CREATE TABLE grade (
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT      NOT NULL,
    course_id  BIGINT      NOT NULL,
    score      DECIMAL(5, 1) NOT NULL,
    term       VARCHAR(20) NOT NULL DEFAULT '2024-2025-1',
    deleted    TINYINT     DEFAULT 0,
    UNIQUE KEY uk_student_course (student_id, course_id),
    KEY idx_course (course_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '成绩';

CREATE TABLE sys_user (
    id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    username  VARCHAR(32) NOT NULL UNIQUE,
    password  VARCHAR(64) NOT NULL,
    role      VARCHAR(16) NOT NULL COMMENT 'teacher / student',
    real_name VARCHAR(32)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '登录用户';
