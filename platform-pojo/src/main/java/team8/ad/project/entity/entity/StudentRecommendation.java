package team8.ad.project.entity.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StudentRecommendation {
    private Long id;
    private Long studentId;
    private String recommendedQuestions; // JSON 字符串
    private LocalDateTime createdAt;
}