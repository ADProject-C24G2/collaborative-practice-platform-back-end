import mysql.connector
from mysql.connector import errorcode

# 修改为你的本地数据库配置
config = {
    'user': 'root',
    'password': 'wqx021201',
    'host': 'localhost',
}

DB_NAME = 'collaborative_practice_platform'

TABLES = {}

# 创建题目表
TABLES['qa'] = """
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
)
"""

# 学生作答记录表
TABLES['student_answer_record'] = """
CREATE TABLE student_answer_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    is_correct TINYINT NOT NULL CHECK (is_correct IN (0, 1)),
    answer INT NOT NULL,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
)
"""

# 学生推荐题目表
TABLES['student_recommendations'] = """
CREATE TABLE student_recommendations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    recommended_questions JSON NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_student_id (student_id)
)
"""

def create_database(cursor):
    try:
        cursor.execute(f"CREATE DATABASE {DB_NAME} DEFAULT CHARACTER SET 'utf8mb4'")
    except mysql.connector.Error as err:
        print(f"数据库创建失败: {err}")
        exit(1)

def main():
    try:
        conn = mysql.connector.connect(**config)
        cursor = conn.cursor()

        # 创建数据库
        try:
            conn.database = DB_NAME
        except mysql.connector.Error as err:
            if err.errno == errorcode.ER_BAD_DB_ERROR:
                create_database(cursor)
                conn.database = DB_NAME
            else:
                print(err)
                exit(1)

        # 创建表
        for table_name, ddl in TABLES.items():
            try:
                print(f"创建表 `{table_name}` ...", end='')
                cursor.execute(ddl)
                print("成功")
            except mysql.connector.Error as err:
                if err.errno == errorcode.ER_TABLE_EXISTS_ERROR:
                    print("已存在")
                else:
                    print(f"失败: {err}")

        cursor.close()
        conn.close()
        print("数据库和表创建完成。")

    except mysql.connector.Error as err:
        print(f"连接数据库失败: {err}")
        exit(1)

if __name__ == '__main__':
    main()
