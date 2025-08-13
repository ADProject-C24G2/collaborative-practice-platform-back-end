```sql
create database collaborative_practice_platform;
```

```sql 创建题目表
CREATE TABLE qa (
    id INT PRIMARY KEY AUTO_INCREMENT,
    image LONGBLOB,        
    question VARCHAR(1000), 
    choices JSON,           
    answer INT,             
    hint TEXT,              
    task VARCHAR(100),      
    grade VARCHAR(20),      
    subject VARCHAR(50),    
    topic VARCHAR(100),     
    category VARCHAR(100),  
    skill VARCHAR(100),     
    lecture TEXT,           
    solution TEXT           
);
```

```sql 创建学生作答记录表
CREATE TABLE student_answer_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    is_correct TINYINT NOT NULL CHECK (is_correct IN (0, 1)),
    answer INT NOT NULL,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

```sql 学生推荐题目表
CREATE TABLE student_recommendations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    recommended_questions JSON NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_student_id (student_id)
);
```

```sql
-- 作业表
CREATE TABLE assignment (
    id INT PRIMARY KEY AUTO_INCREMENT,
    class_id INT NOT NULL,
    assignment_name VARCHAR(255) NOT NULL,
    expire_time DATETIME,
    create_time DATETIME,
    whether_finish TINYINT(1) DEFAULT 0,
    finish_time DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 作业详情表
CREATE TABLE assignment_details (
    id INT PRIMARY KEY AUTO_INCREMENT,
    assignment_id INT NOT NULL,
    question_id INT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

```sql 学生作业表
CREATE TABLE assignment_students_details (
    id INT AUTO_INCREMENT PRIMARY KEY,
    assignmentId INT NOT NULL,
    studentId INT NOT NULL,
    whetherFinish TINYINT(1) DEFAULT 0 COMMENT '0-未完成, 1-已完成',
    finishTime DATETIME DEFAULT NULL,
    Accurancy DECIMAL(5,2) DEFAULT NULL COMMENT '准确率, 例如 85.50',
    UNIQUE KEY uq_assignment_student (assignmentId, studentId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

```sql 通知表
CREATE TABLE announcement (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '公告ID',
    classId INT NOT NULL COMMENT '班级ID',
    studentId INT DEFAULT NULL COMMENT '学生ID（可为空，表示发给全班）',
    teacherId INT NOT NULL COMMENT '教师ID',
    title VARCHAR(255) NOT NULL COMMENT '公告标题',
    content TEXT NOT NULL COMMENT '公告内容',
    createTime DATETIME NOT NULL COMMENT '创建时间',
    status TINYINT(1) NOT NULL COMMENT '状态：0=未读，1=已读'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级公告表';
```

```sql 手动插入部分学生作答信息
INSERT INTO student_answer_record (student_id, question_id, is_correct, answer, timestamp) VALUES
(1, 1, 1, 1, '2025-08-04 09:15:00'),
(1, 2, 0, 2, '2025-08-04 10:30:00'),
(1, 3, 1, 3, '2025-08-04 11:45:00'),
(1, 4, 0, 1, '2025-08-04 13:20:00'),
(1, 5, 1, 2, '2025-08-04 14:50:00'),
(1, 6, 0, 4, '2025-08-04 16:10:00'),
(1, 7, 1, 3, '2025-08-04 17:25:00'),
(1, 8, 0, 1, '2025-08-04 19:00:00'),
(1, 9, 1, 2, '2025-08-04 20:30:00'),
(1, 10, 0, 4, '2025-08-04 22:15:00');
```