package team8.ad.project.entity.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Announcement {
    private Integer id;
    private Integer classId;
    private Integer studentId;
    private Integer teacherId;
    private String title;
    private String content;

    private LocalDateTime createTime;

    private Integer status; // 0=未读, 1=已读
}