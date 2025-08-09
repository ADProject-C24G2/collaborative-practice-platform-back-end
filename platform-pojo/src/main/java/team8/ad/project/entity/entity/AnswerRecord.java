package team8.ad.project.entity.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnswerRecord {
    private Long id;
    private Long studentId; 
    private Long questionId; 
    private Integer isCorrect;
    private Integer answer; // 保持 int 类型
    private LocalDateTime timestamp;
}