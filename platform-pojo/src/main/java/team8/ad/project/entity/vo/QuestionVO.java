package team8.ad.project.entity.vo;

import lombok.Data;

import java.util.List;

@Data
public class QuestionVO {
    private Integer id;
    private byte[] image; // 或 String，如果存储路径
    private String question;
    private List<String> choices; // JSON 字符串
    private Integer answer;
}
