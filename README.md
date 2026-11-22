# 学生成绩管理系统 · Spring Boot 后端

<p>
  <img alt="Java" src="https://img.shields.io/badge/Java-8-orange?logo=openjdk">
  <img alt="Spring Boot" src="https://img.shields.io/badge/Spring%20Boot-2.7.18-6db33f?logo=springboot">
  <img alt="MyBatis-Plus" src="https://img.shields.io/badge/MyBatis--Plus-3.5.3-red">
  <img alt="MySQL" src="https://img.shields.io/badge/MySQL-8.0-4479a1?logo=mysql">
  <img alt="EasyExcel" src="https://img.shields.io/badge/EasyExcel-3.3-2e8b57">
  <img alt="JWT" src="https://img.shields.io/badge/JWT-HS256-d63aff">
  <img alt="coverage" src="https://img.shields.io/badge/coverage-87%25-brightgreen">
</p>

**Java 8 + Spring Boot 2.7 + MyBatis-Plus + MySQL 8 + EasyExcel + JWT**

同一套教务业务的 Java 后端实现：学生 / 课程 / 成绩的 CRUD、统计聚合，以及 **1 万行 Excel 批量导入导出**。
与另一个 Flask 版后端（[grade-admin](https://github.com/jc67486/grade-admin)）业务相同、技术栈不同，用于对比两种后端方案的工程差异。

## 核心数据：Excel 导入优化

| 版本 | 实现方式 | 10000 行耗时 | 吞吐 |
|---|---|---|---|
| 初版 | `for` 循环逐条 `insert`，10000 次 SQL 往返 | **49040 ms** | 204 行/秒 |
| 优化后 | EasyExcel SAX 流式读 + 1000 条/批 `INSERT`（`rewriteBatchedStatements=true`） | **1007 ms** | **9930 行/秒** |

**提速 49 倍。** 两次测量使用同一个 242 KB 的 xlsx 文件、同一台机器、同一张表。

优化点拆解：

1. **读**：`EasyExcel` 用 SAX 逐行解析，不把整个工作簿加载进堆内存（POI 的 `XSSFWorkbook` 会）
2. **攒批**：每 1000 行 flush 一次，把 10000 次往返压到 10 次
3. **写**：`INSERT IGNORE INTO ... VALUES (...),(...),(...),(...)` 单条 SQL 插多行
4. **驱动参数**：JDBC URL 加 `rewriteBatchedStatements=true`，让 MySQL 驱动把多条 INSERT 合并成一个包

## 功能与实现

| 模块 | 说明 |
|---|---|
| 分层架构 | Controller / Service / Mapper 三层，DTO 入参校验、VO 出参隔离，不把实体直接暴露给前端 |
| 统一响应 | `Result<T>` + `@RestControllerAdvice` 全局异常处理，业务异常 400、鉴权 401/403、其余 500 |
| 鉴权 | JWT 签发（HS256，2 小时）+ `HandlerInterceptor` 校验，`ThreadLocal` 存用户上下文，请求结束清理防内存泄漏 |
| 分页查询 | MyBatis-Plus 分页插件 + 条件构造器，支持关键字（姓名/学号）与班级动态筛选 |
| 统计聚合 | 课程均分 / 最高最低 / 及格率、分数段分布，各用一条聚合 SQL 一次返回 |
| Excel 导入 | SAX 流式读 + 分批批量插入 + 失败降级（整批失败时逐条重试） |
| Excel 导出 | EasyExcel 流式写，导出 10048 行生成 243 KB 文件 |

## 接口

| 方法 | 路径 | 权限 | 说明 |
|---|---|---|---|
| POST | `/api/auth/login` | 公开 | 登录签发 JWT |
| GET | `/api/auth/me` | 登录 | 当前用户（从 ThreadLocal 取） |
| GET | `/api/students?page&size&keyword&class` | 登录 | 分页查询 |
| POST/PUT/DELETE | `/api/students[/:id]` | **教师** | 增删改，越权 403 |
| GET | `/api/grades?keyword&courseId` | 登录 | 成绩明细（三表关联） |
| POST | `/api/grades` | **教师** | 录入，重复/越界 400 |
| GET | `/api/stats` | 登录 | 课程统计 + 分数段 |
| POST | `/api/excel/import` | **教师** | 导入学生（multipart） |
| GET | `/api/excel/export` | 登录 | 导出名单 |
| GET | `/api/excel/template` | 登录 | 下载导入模板 |

统一响应：`{ "code": 0, "msg": "ok", "data": ... }`；HTTP 状态码与 code 对齐（401/403/400/500）。

## 测试

`ash
mvn test        # 31 passed
`

JaCoCo 报告在 	arget/site/jacoco/index.html。

| 指标 | 覆盖率 |
|---|---|
| 指令 | 88.2%（1047/1187） |
| 分支 | 72.9%（35/48） |
| 行 | 87.2%（238/273） |
| 方法 | 88.7%（63/71） |

> 数字是排除 Lombok 生成代码后的结果：lombok.config 里开 ddLombokGeneratedAnnotation，
> 否则 getter/setter/equals/hashCode 会把指令覆盖拉低到 30% 附近。

覆盖点：

- **鉴权**：登录成功/密码错误/用户名为空、无 token 401、伪造 token 401、/me 取 ThreadLocal 用户、健康检查免鉴权
- **权限**：学生角色新增/删除学生 403、学生角色录入成绩 403、学生角色导入 Excel 403
- **参数校验**：学号非 8 位、成绩超 100、成绩为负、学号重复（业务异常 400）
- **分页与筛选**：分页返回 list/total、学号关键字搜索、班级筛选
- **Excel**：正常导入（total/success/failed/costMs 四个字段）、只有表头时返回 0 行、空字节文件 4xx、导出 Content-Disposition、模板下载
- **统计**：课程数固定 6、分数段非空、均分落在 0–100 区间

每个用例都加 @Transactional 自动回滚，**跑完不留脏数据**。

## 快速开始

### 前置

- JDK 8+（本项目在 Temurin 1.8.0_422 上验证）
- MySQL 8.0（默认连 `localhost:3305`，改 `application.yml` 即可）
- Maven 3.6+（或用 IDE 内置）

### 初始化数据库

```bash
# 建库
mysql -uroot -p -e "CREATE DATABASE grade_java DEFAULT CHARSET utf8mb4;"

# 建表 + 初始数据（必须用 utf8mb4 客户端，否则中文会存成乱码）
mysql -uroot -p --default-character-set=utf8mb4 grade_java < src/main/resources/db/schema.sql
mysql -uroot -p --default-character-set=utf8mb4 grade_java < src/main/resources/db/data.sql
```

`data.sql` 用递归 CTE 生成 48 名学生、6 门课程，再用 `CROSS JOIN` 生成 288 条成绩。

### 启动

```bash
mvn spring-boot:run
# 或
mvn clean package -DskipTests && java -jar target/grade-springboot-1.0.0.jar
```

服务地址 `http://127.0.0.1:5003`，账号：

| 用户名 | 密码 | 角色 |
|---|---|---|
| `teacher` | `123456` | 教师（可增删改、导入导出） |
| `student` | `123456` | 学生（只读） |

### 验证

```bash
# 登录拿 token
curl -X POST http://127.0.0.1:5003/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"teacher\",\"password\":\"123456\"}"

# 带 token 查学生
curl http://127.0.0.1:5003/api/students?page=1&size=5 -H "Authorization: Bearer <token>"

# 无 token 应返回 HTTP 401
curl -i http://127.0.0.1:5003/api/students
```

## 踩坑记录

1. **中文全部变成 `ï¿½`**：`spring.sql.init` 读 SQL 脚本时默认用平台编码（Windows 是 GBK），UTF-8 脚本被按 GBK 解释，部分字节无法映射变成替换字符，连同 `continue-on-error` 一起写进了库。解决：显式加 `spring.sql.init.encoding: UTF-8`，或改用 `mysql --default-character-set=utf8mb4` 客户端导入。
2. **10000 行导入跑了 49 秒**：`for` 循环逐条 `insert`，每条一次网络往返。改批量后降到 1 秒（见上方对比表）。
3. **批量插入遇到重复学号整批失败**：`INSERT IGNORE` 让重复行跳过而不中断整批；同时保留降级逻辑——整批失败时退回逐条插入，保证部分数据能落地。
4. **401/403 全返回 400**：`@ResponseStatus(BAD_REQUEST)` 写在 `BizException` 处理器上，`AuthException` 继承它就被一起吞了。改为 `AuthException` 单独处理并返回 `ResponseEntity.status(401/403)`。
5. **`mvn package` 报 `Unable to rename .jar`**：Windows 下 jar 被运行中的进程锁住，先停进程再打包。
6. **`SELECT ... WHERE id IN (...)` 参数过多**：分批时把 `IN` 列表控制在 1000 以内，避免超过 `max_allowed_packet`。

## 目录结构

```
grade-springboot/
├── src/main/java/com/zhang/grade/
│   ├── common/        Result / PageResult / BizException / GlobalExceptionHandler
│   ├── config/        MyBatis-Plus 分页、拦截器注册、字段自动填充
│   ├── security/      JwtUtil / JwtInterceptor / UserContext(ThreadLocal)
│   ├── entity/        Student / Course / Grade / SysUser
│   ├── mapper/        MyBatis-Plus BaseMapper + 注解 SQL（三表关联、聚合、批量插入）
│   ├── dto/           入参对象（带 JSR-303 校验注解）
│   ├── vo/            出参对象 + Excel 模型（@ExcelProperty）
│   ├── service/       Auth / Student / Grade / Stats / Excel
│   └── controller/    Auth / Student / Grade / Stats / Excel / Health
└── src/main/resources/
    ├── application.yml
    └── db/            schema.sql / data.sql
```

## 与 Flask 版的对比

| 维度 | Flask 版（grade-admin） | Spring Boot 版（本项目） |
|---|---|---|
| 语言 / 框架 | Python 3 + Flask + SQLAlchemy | Java 8 + Spring Boot 2.7 + MyBatis-Plus |
| 鉴权 | PyJWT + 装饰器 | jjwt + HandlerInterceptor + ThreadLocal |
| 参数校验 | 手写（类型/边界判断） | JSR-303 注解（`@NotNull` / `@DecimalMax`） |
| 异常处理 | `@app.errorhandler` | `@RestControllerAdvice` |
| 测试 | pytest 29 例，覆盖率 94% | JUnit 5 + MockMvc 31 例，行覆盖 87% |
| 适合场景 | 快速原型、数据脚本 | 企业级后端、需要严格分层与类型约束 |
