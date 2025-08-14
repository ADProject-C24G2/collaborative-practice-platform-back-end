package team8.ad.project.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class RecommendationRequestDTO {
    private List<Long> questionIds; // 推荐的题目 ID 列表
    private long studentId;
}