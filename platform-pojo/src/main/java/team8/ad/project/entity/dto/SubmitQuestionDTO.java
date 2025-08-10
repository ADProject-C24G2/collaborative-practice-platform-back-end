package team8.ad.project.entity.dto;

import lombok.Data;
import java.util.List;

@Data
public class SubmitQuestionDTO {
    private Integer id;            // 题目ID（qa.id）
    private byte[]  image;         // 图片（二进制）
    private String  question;      // 题干
    private List<String> choices;  // 选项
}