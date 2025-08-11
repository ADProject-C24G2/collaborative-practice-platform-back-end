package team8.ad.project.entity.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Announcement {
    private Integer id;            // 公告ID，主键
    private Integer classId;       // 班级ID
    private Integer studentId;     // 学生ID（可为空，表示发给全班）
    private Integer teacherId;     // 教师ID
    private String title;          // 公告标题
    private String content;        // 公告内容
    private LocalDateTime createTime; // 创建时间
    private Byte status;           // 状态：0=未读，1=已读
    private String recipientType;

}


