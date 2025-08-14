package team8.ad.project.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class RecommendationDTO {
    private List<AnswerRecordItem> records;
    private long studentId;

    @Data
    public static class AnswerRecordItem {
        private Long questionId;
        private Integer isCorrect;
    }
}