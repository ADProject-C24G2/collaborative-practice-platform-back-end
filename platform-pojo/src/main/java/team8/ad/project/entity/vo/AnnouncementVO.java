package team8.ad.project.entity.vo;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AnnouncementVO {

    private String title;

    private LocalDateTime createTime;

    private String recipientType;

    private String content;

    private List<Student> students;

    @Data
    public static class Student {
        private int studentId;
        private String studentName;
    }
}


